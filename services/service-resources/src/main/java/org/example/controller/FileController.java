package org.example.controller;

import lombok.extern.log4j.Log4j2;
import org.example.model.ApiStatus;
import org.example.model.ResourcesApiResponse;
import org.example.model.Result;
import org.example.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.net.URLEncoder;

@Log4j2
@RestController
@RequestMapping("/api/resources")
public class FileController {

    @Autowired
    private FileService fileService;

    /**
     * 上传文件
     */
    @PostMapping("/upload")
    public ResponseEntity<ResourcesApiResponse<?>> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            if (file == null || file.isEmpty()) {
                return ResponseEntity.status(ApiStatus.POST_FAIL.getCode())
                        .body(ResourcesApiResponse.postFail("文件不能为空"));
            }
            final String filePath = fileService.uploadFile(file);
            log.info("文件上传成功，文件路径：{}", filePath);
            if (filePath == null) {
                return ResponseEntity.status(ApiStatus.POST_FAIL.getCode())
                        .body(ResourcesApiResponse.postFail("文件上传失败"));
            }
            return ResponseEntity.status(ApiStatus.POST_SUCCESS.getCode())
                    .body(ResourcesApiResponse.postSuccess(filePath));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 上传文件到指定目录
     */
    @PostMapping("/upload/{directory}")
    public Result<String> uploadFileToDirectory(
            @RequestParam("file") MultipartFile file,
            @PathVariable("directory") String directory) {
        try {
            if (file == null || file.isEmpty()) {
                return Result.error("文件不能为空");
            }
            String filePath = fileService.uploadFile(file, directory);
            if (filePath == null) {
                return Result.error("文件上传失败");
            }
            return Result.success(filePath);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("文件上传异常：" + e.getMessage());
        }
    }

    /**
     * 下载文件
     */
    @GetMapping("/download/{filePath}")
    public ResponseEntity<byte[]> downloadFile(
            @PathVariable("filePath") String filePath) {
        try {
            InputStream inputStream = fileService.downloadFile(filePath);
            if (inputStream == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            // 读取文件内容
            byte[] bytes = inputStream.readAllBytes();
            inputStream.close();

            // 设置响应头
            HttpHeaders headers = new HttpHeaders();
            // 从filePath中提取文件名
            String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
            // 编码文件名，防止中文乱码
            String encodedFileName = URLEncoder.encode(fileName, "UTF-8");
            headers.setContentDispositionFormData("attachment", encodedFileName);
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentLength(bytes.length);

            return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 删除文件
     */
    @DeleteMapping("/{filePath}")
    public Result<Boolean> deleteFile(@PathVariable("filePath") String filePath) {
        try {
            boolean result = fileService.deleteFile(filePath);
            if (result) {
                return Result.success(true);
            } else {
                return Result.error("文件删除失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("文件删除异常：" + e.getMessage());
        }
    }

    /**
     * 获取文件临时访问链接
     */
    @GetMapping("/url/{filePath}")
    public Result<String> getFileUrl(
            @PathVariable("filePath") String filePath,
            @RequestParam(defaultValue = "604800") int expiry) {
        try {
            String url = fileService.getFileUrl(filePath, expiry);
            if (url == null) {
                return Result.error("获取文件链接失败");
            }
            return Result.success(url);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取文件链接异常：" + e.getMessage());
        }
    }
}