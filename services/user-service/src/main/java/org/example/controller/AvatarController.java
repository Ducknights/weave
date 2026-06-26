package org.example.controller;

import org.example.model.eunms.UserApiStatus;
import org.example.service.FileService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users/avatar")
public class AvatarController {

    private final FileService fileService;

    public AvatarController(FileService fileService) {
        this.fileService = fileService;
    }

    /**
     * 上传用户头像
     * @param file 头像文件
     * @return 头像路径
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadAvatar(
            @RequestParam("file") MultipartFile file) {
        String path = fileService.uploadAvatar(file);
        return ResponseEntity.ok(UserApiStatus.CREATE_SUCCESS.response(path));
    }

    /**
     * 获取文件预签名URL
     * @param path 文件路径
     * @param expiry 有效期（秒），默认3600秒
     * @return 预签名URL
     */
    @GetMapping("/url")
    public ResponseEntity<?> getFileUrl(
            @RequestParam String path,
            @RequestParam(defaultValue = "3600") int expiry) {

        String url = fileService.getFileUrl(path, expiry);
        return ResponseEntity.ok(UserApiStatus.GET_SUCCESS.response(url));
    }

    /**
     * 获取文件预签名URL
     * @param paths 文件路径列表
     * @param expiry 有效期（秒），默认3600秒
     * @return 预签名URL列表
     */
    @GetMapping("/urls")
    public ResponseEntity<?> getFileUrls(
            @RequestParam List<String> paths,
            @RequestParam(defaultValue = "3600") int expiry) {

        Map<String, String> urls = fileService.getFileUrls(paths, expiry);

        return ResponseEntity.ok(UserApiStatus.GET_SUCCESS.response(urls));
    }
}
