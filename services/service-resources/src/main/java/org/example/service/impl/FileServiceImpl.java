package org.example.service.impl;

import org.example.service.FileService;
import org.example.utils.MinioUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private MinioUtil minioUtil;

    /**
     * 生成唯一的文件名
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
    public String uploadFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        String fileName = generateFileName(file);
        return minioUtil.uploadFile(fileName, file);
    }

    @Override
    public String uploadFile(MultipartFile file, String directory) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        String fileName = generateFileName(file);
        String objectName = directory + "/" + fileName;
        return minioUtil.uploadFile(objectName, file);
    }

    @Override
    public InputStream downloadFile(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return null;
        }
        return minioUtil.getFile(filePath);
    }

    @Override
    public boolean deleteFile(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return false;
        }
        return minioUtil.deleteFile(filePath);
    }

    @Override
    public String getFileUrl(String filePath, int expiry) {
        if (filePath == null || filePath.isEmpty()) {
            return null;
        }
        return minioUtil.getPresignedUrl(filePath, expiry);
    }

    @Override
    public String getFileUrl(String filePath) {
        // 默认7天过期
        return getFileUrl(filePath, 7 * 24 * 60 * 60);
    }
}