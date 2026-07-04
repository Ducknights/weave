package com.weave.minio.service.impl;

import com.weave.minio.util.ImageCompressUtil;
import com.weave.minio.util.MimeTypeUtil;
import com.weave.minio.util.MinioUtil;
import lombok.extern.slf4j.Slf4j;
import com.weave.redis.annotation.RedisCacheable;
import com.weave.redis.constant.CacheKey;
import com.weave.minio.service.FileService;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.*;

@Slf4j
public class FileServiceImpl implements FileService {

    private final MinioUtil minioUtil;

    public FileServiceImpl(MinioUtil minioUtil) {
        this.minioUtil = minioUtil;
    }

    /**
     * 生成文件名
     */
    private String generateFileName(String originalFilename) {
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        return UUID.randomUUID() + extension;
    }

    /**
     * 上传头像，尺寸限制为512x512，质量压缩
     */
    @Override
    public String uploadAvatar(MultipartFile file) {
        // 生成文件名，确保文件名唯一
        String objectName = generateFileName(file.getOriginalFilename());
        // 如果是图片，执行压缩操作
        if (MimeTypeUtil.isImage(file.getContentType())) {
            // 压缩图片(限制大小为512x512，质量压缩)
            byte[] compressed = ImageCompressUtil.compressBySize(file, 512, 512, 0.6f);
            return minioUtil.uploadBytes(objectName, compressed, file.getContentType());
        }else {
            throw new IllegalArgumentException("上传的文件不是图片，请上传图片格式");
        }

    }

    /**
     * 上传文件，支持图片和视频，图片质量压缩，视频直接上传
     */
    @Override
    public List<String> uploadFiles(List<MultipartFile> files) {
        List<String> objectNames = new ArrayList<>();
        for (MultipartFile file : files) {
            String objectName = generateFileName(file.getOriginalFilename());
            if (MimeTypeUtil.isImage(file.getContentType())){
                // 压缩图片(大小不变，质量压缩)
                byte[] compressed = ImageCompressUtil.compressByScale(file, 1.0f, 0.6f);
                // 上传压缩后的图片到MinIO
                minioUtil.uploadBytes(objectName, compressed, file.getContentType());
            }else if (MimeTypeUtil.isVideo(file.getContentType())){
                // 视频直接上传
                minioUtil.uploadFile(objectName, file);
            }else {
                throw new IllegalArgumentException("上传的文件类型不支持");
            }
            objectNames.add(objectName);
        }
        return objectNames;
    }

    @Override
    public InputStream downloadFile(String objectName) {
        return minioUtil.getFile(objectName);
    }

    @Override
    public boolean deleteFile(String objectName) {
        return minioUtil.deleteFile(objectName);
    }

    @Override
    @RedisCacheable(value = CacheKey.FILE_URL, key = "#objectName")
    public String getFileUrl(String objectName, int expiry) {
        return minioUtil.getPresignedUrl(objectName, expiry);
    }

    @Override
    public Map<String, String> getFileUrls(List<String> objectNames, int expiry) {
        return objectNames.parallelStream()
                .filter(objectName -> objectName != null && !objectName.trim().isEmpty())
                .collect(HashMap::new, (map, objectName) -> {
                    try {
                        String url = minioUtil.getPresignedUrl(objectName, expiry);
                        map.put(objectName, url);
                    } catch (Exception e) {
                        log.warn("获取文件URL失败: {}", objectName, e);
                        map.put(objectName, null);
                    }
                }, HashMap::putAll);
    }
}
