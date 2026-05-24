package org.example.controller;

import jakarta.annotation.Resource;
import org.example.service.PostCommandService;
import org.example.util.SecurityUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/posts")
public class ActionController {

    @Resource
    private PostCommandService postCommandService;

    /**
     * 点赞帖子
     */
    @PostMapping("/{id}/like")
    public ResponseEntity<Map<String, Object>> like(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        postCommandService.like(userId, id);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);

        return ResponseEntity.ok(result);
    }

    /**
     * 取消点赞帖子
     */
    @PostMapping("/{id}/unlike")
    public ResponseEntity<Map<String, Object>> unlike(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        postCommandService.unlike(userId, id);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);

        return ResponseEntity.ok(result);
    }

    /**
     * 收藏帖子
     */
    @PostMapping("/{id}/collect")
    public ResponseEntity<Map<String, Object>> collect(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        postCommandService.collect(userId, id);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);

        return ResponseEntity.ok(result);
    }

    /**
     * 取消收藏帖子
     */
    @PostMapping("/{id}/uncollect")
    public ResponseEntity<Map<String, Object>> unfavorite(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        postCommandService.uncollect(userId, id);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);

        return ResponseEntity.ok(result);
    }

    /**
     * 分享帖子
     */
    @PostMapping("/{id}/share")
    public ResponseEntity<Map<String, Object>> sharePost(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        postCommandService.sharePost(userId, id);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);

        return ResponseEntity.ok(result);
    }
}