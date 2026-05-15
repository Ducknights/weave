package org.example.controller;

import org.example.util.MinioUtil;
import org.example.util.MimeTypeUtil;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/users/avatar")
public class AvatarController {

    private final MinioUtil minioUtil;

    public AvatarController(MinioUtil minioUtil) {
        this.minioUtil = minioUtil;
    }

    /**
     * 上传用户头像
     * @param file 头像文件
     * @return 头像路径
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> uploadAvatar(@RequestParam("file") MultipartFile file) {
        String path = uploadFile(file);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("path", path);
        
        return ResponseEntity.ok(result);
    }

    /**
     * 上传文件到 MinIO
     * @param file 文件
     * @return 存储路径
     */
    private String uploadFile(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String fileName = UUID.randomUUID().toString() + extension;
        String directory = MimeTypeUtil.getDirectoryByMimeType(file.getContentType());
        String objectName = directory + "/avatar/" + fileName;
        
        return minioUtil.uploadFile("default", objectName, file);
    }
}
