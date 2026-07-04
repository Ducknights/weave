package com.weave.minio.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface FileService {

    /**
     * 上传头像
     */
    String uploadAvatar(MultipartFile file);

    /**
     * 上传多个文件
     */
    List<String> uploadFiles(List<MultipartFile> files);

    /**
     * 下载文件
     */
    InputStream downloadFile(String objectName);

    /**
     * 删除文件
     */
    boolean deleteFile(String objectName);

    /**
     * 获取文件临时访问链接
     */
    String getFileUrl(String objectName, int expiry);

    /**
     * 批量获取文件临时访问链接
     * @param objectNames 文件路径列表
     * @param expiry 有效期（秒）
     * @return 文件路径与预签名URL的映射
     */
    Map<String, String> getFileUrls(List<String> objectNames, int expiry);
}
