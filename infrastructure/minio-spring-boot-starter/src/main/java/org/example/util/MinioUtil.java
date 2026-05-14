package org.example.util;

import io.minio.*;
import io.minio.http.Method;
import lombok.extern.slf4j.Slf4j;
import org.example.config.MinioConfig;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

@Slf4j
public class MinioUtil {

    private final MinioClient minioClient;
    private final MinioConfig minioConfig;

    public MinioUtil(MinioClient minioClient, MinioConfig minioConfig) {
        this.minioClient = minioClient;
        this.minioConfig = minioConfig;
    }

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
            log.info("文件上传成功：[{}] -> [{}]", bucketName, objectName);
            return objectName;
        } catch (Exception e) {
            log.error("文件上传失败：[{}] -> [{}]", bucketName, objectName, e);
            throw new RuntimeException("文件上传失败：" + e.getMessage(), e);
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
            log.error("获取文件失败：[{}] -> [{}]", bucketName, objectName, e);
            throw new RuntimeException("获取文件失败：" + e.getMessage(), e);
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
            log.info("文件删除成功：[{}] -> [{}]", bucketName, objectName);
            return true;
        } catch (Exception e) {
            log.error("文件删除失败：[{}] -> [{}]", bucketName, objectName, e);
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
     * 获取文件 URL（临时链接）
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
            log.error("获取文件 URL 失败：[{}] -> [{}]", bucketName, objectName, e);
            throw new RuntimeException("获取文件 URL 失败：" + e.getMessage(), e);
        }
    }

    /**
     * 获取文件 URL（使用默认桶）
     */
    public String getPresignedUrl(String objectName, int expiry) {
        return getPresignedUrl(minioConfig.getBucket(), objectName, expiry);
    }
}
