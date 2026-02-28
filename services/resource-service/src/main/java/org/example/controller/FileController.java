package org.example.controller;

import jakarta.annotation.Resource;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;

import org.example.dto.FileInfoDto;
import org.example.dto.ResultDto;
import org.example.model.ApiStatus;
import org.example.model.ApiResult;
import org.example.service.FileService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriUtils;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Log4j2
@RestController
@RequestMapping("/api/resources")
public class FileController {

    @Resource
    private FileService fileService;

    /**
     * 上传文件
     */
    @PostMapping()
    public ApiResult<?> uploadFileToDirectory(@NonNull @RequestParam("file") List<MultipartFile> files) {
        try {
            if (files.isEmpty()) {
               throw new IllegalArgumentException("文件不能为空");
            }
            List<FileInfoDto> filePathList = fileService.uploadFile(files);
            ResultDto resultDto = new ResultDto(filePathList);
            return ApiStatus.POST_SUCCESS.response(resultDto);
        } catch (Exception e) {
            log.error("文件上传异常：{}", e.getMessage());
            return ApiStatus.POST_FAIL.response("文件上传异常：" + e.getMessage());
        }
    }

    /**
     * 下载文件
     */
    @GetMapping("/{filePath}")
    public ResponseEntity<org.springframework.core.io.Resource> downloadFile(@PathVariable("filePath") String filePath) {
        try {
            // 1. 从服务获取文件流
            InputStream inputStream = fileService.downloadFile(filePath);

            // 2. 将 InputStream 包装为 Spring 的 Resource
            InputStreamResource resource = new InputStreamResource(inputStream);

            // 3. 从filePath中提取文件名并编码
            String fileName = filePath.substring(filePath.lastIndexOf('/') + 1);
            String encodedFileName = UriUtils.encode(fileName, StandardCharsets.UTF_8);

            // 4. 构建响应头
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFileName + "\"");
            headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
            // 注意：Content-Length 通常由Spring自动处理，除非你知道确切大小且需要手动设置

            // 5. 返回 ResponseEntity，body是Resource，Spring会处理流式传输
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);

        } catch (Exception e) {
            log.error("文件下载异常：{}", filePath, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 删除文件
     */
    @DeleteMapping("/{filePath}")
    public ApiResult<?> deleteFile(@NonNull @PathVariable("filePath") String filePath) {
        if (filePath.isEmpty()) {
            throw new IllegalArgumentException("文件路径不能为空");
        }
        try {
            boolean result = fileService.deleteFile(filePath);
            if (result) {
                return ApiStatus.DELETE_SUCCESS.response();
            } else {
                return ApiStatus.DELETE_FAIL.response("文件删除失败");
            }
        } catch (Exception e) {
            log.error("文件删除异常：{}", filePath, e);
            return ApiStatus.DELETE_FAIL.response("文件删除异常：" + e.getMessage());
        }
    }

    /**
     * 获取文件临时访问链接
     */
    @GetMapping("/url/{*filePath}")
    public ApiResult<String> getFileUrl(
            @NonNull @PathVariable("filePath") String filePath,
            @RequestParam(defaultValue = "3600") int expiry) {
        if (filePath.isEmpty()) {
            throw new IllegalArgumentException("文件路径不能为空");
        }
        try {
            String url = fileService.getFileUrl(filePath, expiry);
            return ApiStatus.GET_SUCCESS.response(url);
        } catch (Exception e) {
            log.error("获取文件链接异常：{}", filePath, e);
            return ApiStatus.GET_FAIL.response("获取文件链接异常：" + e.getMessage());
        }
    }
}