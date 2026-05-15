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
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/posts/images")
public class PostFileController {

    private final MinioUtil minioUtil;

    public PostFileController(MinioUtil minioUtil) {
        this.minioUtil = minioUtil;
    }

    /**
     * 上传帖子图片
     * @param files 图片文件列表
     * @return 上传的图片路径列表
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> uploadImages(@RequestParam("files") List<MultipartFile> files) {
        List<String> paths = files.stream()
                .map(this::uploadSingleFile)
                .collect(Collectors.toList());
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("paths", paths);
        result.put("count", paths.size());
        
        return ResponseEntity.ok(result);
    }

    /**
     * 上传单个文件
     * @param file 文件
     * @return 存储路径
     */
    private String uploadSingleFile(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String fileName = UUID.randomUUID().toString() + extension;
        String directory = MimeTypeUtil.getDirectoryByMimeType(file.getContentType());
        String objectName = directory + "/post/" + fileName;
        
        return minioUtil.uploadFile("default", objectName, file);
    }
}
