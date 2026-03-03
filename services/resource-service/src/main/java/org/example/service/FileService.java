package org.example.service;

import io.minio.errors.MinioException;
import org.example.dto.FileInfoDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

public interface FileService {

    List<FileInfoDto> uploadFile(List<MultipartFile> files);

    InputStream downloadFile(String filePath);

    boolean deleteFile(String filePath);

    String getFileUrl(String filePath, int expiry);
}
