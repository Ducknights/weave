package org.example.service;

import io.minio.errors.MinioException;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.InputStream;

public interface FileService {

    /**
     * 上传文件
     * @param file 上传的文件
     * @return 文件存储路径
     */
    String uploadFile(MultipartFile file);

    /**
     * 上传文件到指定目录
     * @param file 上传的文件
     * @param directory 目录路径
     * @return 文件存储路径
     */
    String uploadFile(MultipartFile file, String directory);

    /**
     * 下载文件
     * @param filePath 文件路径
     * @return 文件输入流
     */
    InputStream downloadFile(String filePath);

    /**
     * 删除文件
     * @param filePath 文件路径
     * @return 是否删除成功
     */
    boolean deleteFile(String filePath);

    /**
     * 获取文件的临时访问链接
     * @param filePath 文件路径
     * @param expiry 过期时间（秒）
     * @return 文件访问链接
     */
    String getFileUrl(String filePath, int expiry);

    /**
     * 获取文件的临时访问链接（默认过期时间：7天）
     * @param filePath 文件路径
     * @return 文件访问链接
     */
    String getFileUrl(String filePath);
}