package org.example.controller;

import org.example.model.enums.PostApiStatus;
import org.example.service.FileService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/posts/files")
public class PostFileController {

    private final FileService fileService;

    public PostFileController(FileService fileService) {
        this.fileService = fileService;
    }

    /**
     * 上传帖子图片
     * @param files 图片文件列表
     * @return 上传的图片路径列表
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadImages(@RequestParam("files") List<MultipartFile> files) {
        List<String> paths = fileService.uploadFiles(files, "images/post");
        return ResponseEntity.ok(PostApiStatus.CREATE_SUCCESS.response(paths));
    }

    /**
     * 获取单个文件预签名URL
     * @param path 文件路径
     * @param expiry 有效期（秒），默认3600秒
     * @return 预签名URL
     */
    @GetMapping("/url")
    public ResponseEntity<?> getFileUrl(
            @RequestParam String path,
            @RequestParam(defaultValue = "3600") int expiry) {
        String url = fileService.getFileUrl(path, expiry);
        return ResponseEntity.ok(PostApiStatus.SUCCESS.response(url));
    }

    /**
     * 批量获取文件预签名URL
     * @param paths 文件路径列表
     * @param expiry 有效期（秒），默认3600秒
     * @return 文件路径与预签名URL的映射
     */
    @GetMapping("/urls")
    public ResponseEntity<?> getFileUrls(
            @RequestParam List<String> paths,
            @RequestParam(defaultValue = "3600") int expiry) {
        Map<String, String> urlMap = fileService.getFileUrls(paths, expiry);
        return ResponseEntity.ok(PostApiStatus.SUCCESS.response(urlMap));
    }
}
