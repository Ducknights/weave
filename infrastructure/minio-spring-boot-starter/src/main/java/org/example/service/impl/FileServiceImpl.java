package org.example.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.service.FileService;
import org.example.util.MinioUtil;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

    @Override
    public String uploadFile(MultipartFile file, String objectName) {
        if (objectName == null || objectName.isEmpty()) {
            objectName = generateFileName(file.getOriginalFilename());
        }
        return minioUtil.uploadFile(objectName, file);
    }

    @Override
    public List<String> uploadFiles(List<MultipartFile> files, String directory) {
        List<String> objectNames = new ArrayList<>();
        for (MultipartFile file : files) {
            String fileName = generateFileName(file.getOriginalFilename());
            String objectName = (directory != null && !directory.isEmpty() ? directory + "/" : "") + fileName;
            minioUtil.uploadFile(objectName, file);
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
    public String getFileUrl(String objectName, int expiry) {
        return minioUtil.getPresignedUrl(objectName, expiry);
    }
}
