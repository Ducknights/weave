package org.example.service.impl;

import org.example.dto.FileInfoDto;
import org.example.entity.Resources;
import org.example.mapper.ResourceMapper;
import org.example.service.FileService;
import org.example.constant.CacheKey;
import org.example.utils.MimeTypeUtil;
import org.example.utils.MinioUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private MinioUtil minioUtil;
    @Autowired
    private ResourceMapper resourceMapper;

    /**
     * 生成文件名
     *
     * @param file 文件
     * @return 文件名
     */
    private String generateFileName(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        return UUID.randomUUID() + extension;
    }

    @Override
    public List<FileInfoDto> uploadFile(List<MultipartFile> files) {
        Long userId = 1L;
        Map<String, String> pathMap = files.stream()
                .collect(Collectors.toMap(
                        MultipartFile::getOriginalFilename, file -> {
                            // 生成文件名
                            String fileName = generateFileName(file);
                            // 判断文件类型，设置存储目录
                            String directory = MimeTypeUtil.getDirectoryByMimeType(file.getContentType());
                            // 设置对象名，格式为：目录/文件名
                            String objectName = directory + "/" + fileName;
                            // 上传文件，并返回文件path
                            return minioUtil.uploadFile(objectName, file);
                        }
                ));
        //保存到数据库
        List<Resources> resourcesList = pathMap.entrySet().stream()
                .map(entry -> new Resources(userId, entry.getKey(), entry.getValue()))
                .toList();
        resourceMapper.insert(resourcesList);
        // 构建返回结果
        return resourcesList.stream()
                .map(res -> new FileInfoDto(res.getId(), res.getName(), res.getPath()))
                .toList();
    }

    @Override
    public InputStream downloadFile(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            throw new IllegalArgumentException("文件路径不能为空");
        }
        return minioUtil.getFile(filePath);
    }

    @Override
    @CacheEvict(value = CacheKey.PRESENTED_URL_AREA,key = "#filePath")
    public boolean deleteFile(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return false;
        }
        return minioUtil.deleteFile(filePath);
    }

    @Override
    @Cacheable(value = CacheKey.PRESENTED_URL_AREA,key = "#filePath")
    public String getFileUrl(String filePath, int expiry) {
        return minioUtil.getPresignedUrl(filePath, expiry);
    }
}
