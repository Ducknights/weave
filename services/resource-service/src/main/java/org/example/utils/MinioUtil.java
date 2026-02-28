package org.example.utils;

import io.minio.*;
import io.minio.http.Method;
import lombok.extern.log4j.Log4j2;
import org.example.config.MinioConfig;
import org.example.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

@Log4j2
@Component
public class MinioUtil {

    @Autowired
    private MinioClient minioClient;

    @Autowired
    private MinioConfig minioConfig;

    /**
     * 上传文件
     */
    public String uploadFile(String bucketName, String objectName, MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(inputStream, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build());
            return objectName;
        } catch (Exception e) {
            log.error("文件上传失败: [{}] -> [{}]", bucketName, objectName, e);
            return null;
        }
    }

    /**
     * 上传文件（使用默认桶）
     */
    public String uploadFile(String objectName, MultipartFile file) {
        return uploadFile(minioConfig.getBucket(), objectName, file);
    }

    /**
     * 获取文件流
     */
    public InputStream getFile(String bucketName, String objectName) {
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build());
        } catch (Exception e) {
            log.error("获取文件失败: [{}] -> [{}]", bucketName, objectName, e);
            throw new ResourceNotFoundException(e.getMessage());
        }
    }

    /**
     * 获取文件流（使用默认桶）
     */
    public InputStream getFile(String objectName) {
        return getFile(minioConfig.getBucket(), objectName);
    }

    /**
     * 删除文件
     */
    public boolean deleteFile(String bucketName, String objectName) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除文件（使用默认桶）
     */
    public boolean deleteFile(String objectName) {
        return deleteFile(minioConfig.getBucket(), objectName);
    }

    /**
     * 获取文件URL（临时链接）
     */
    public String getPresignedUrl(String bucketName, String objectName, int expiry) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(objectName)
                            .expiry(expiry, TimeUnit.SECONDS)
                            .build());
        } catch (Exception e) {
            log.error("获取文件URL失败: [{}] -> [{}]", bucketName, objectName, e);
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 获取文件URL（使用默认桶）
     */
    public String getPresignedUrl(String objectName, int expiry) {
        return getPresignedUrl(minioConfig.getBucket(), objectName, expiry);
    }
}
