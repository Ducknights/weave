package org.example.controller;

import org.example.util.MinioUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
public class FileUrlController {

    private final MinioUtil minioUtil;

    public FileUrlController(MinioUtil minioUtil) {
        this.minioUtil = minioUtil;
    }

    /**
     * 获取文件预签名URL
     * @param path 文件路径
     * @param expiry 有效期（秒），默认3600秒
     * @return 预签名URL
     */
    @GetMapping("/url")
    public ResponseEntity<Map<String, Object>> getFileUrl(
            @RequestParam String path,
            @RequestParam(defaultValue = "3600") int expiry) {
        
        String url = minioUtil.getPresignedUrl(path, expiry);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("url", url);
        
        return ResponseEntity.ok(result);
    }

    /**
     * 内部接口：获取文件预签名URL（返回纯URL）
     * @param filePath 文件路径
     * @param expiry 有效期（秒）
     * @return 预签名URL
     */
    @GetMapping("/internal/presign")
    public String getPresignedUrl(
            @RequestParam String filePath,
            @RequestParam(defaultValue = "3600") int expiry) {
        return minioUtil.getPresignedUrl(filePath, expiry);
    }
}
