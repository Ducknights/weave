package org.example.controller;

import jakarta.annotation.Resource;
import org.example.model.enums.PostApiStatus;
import org.example.service.PostCommandService;
import org.example.util.SecurityUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/posts")
public class ActionController {

    @Resource
    private PostCommandService postCommandService;

    /**
     * 点赞帖子
     */
    @PostMapping("/{id}/like")
    public ResponseEntity<?> like(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        postCommandService.like(userId, id);
        return ResponseEntity.ok(PostApiStatus.LIKE_SUCCESS.response());
    }

    /**
     * 取消点赞帖子
     */
    @PostMapping("/{id}/unlike")
    public ResponseEntity<?> unlike(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        postCommandService.unlike(userId, id);
        return ResponseEntity.ok(PostApiStatus.UNLIKE_SUCCESS.response());
    }

    /**
     * 收藏帖子
     */
    @PostMapping("/{id}/collect")
    public ResponseEntity<?> collect(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        postCommandService.collect(userId, id);
        return ResponseEntity.ok(PostApiStatus.FAVORITE_SUCCESS.response());
    }

    /**
     * 取消收藏帖子
     */
    @PostMapping("/{id}/uncollect")
    public ResponseEntity<?> unfavorite(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        postCommandService.uncollect(userId, id);
        return ResponseEntity.ok(PostApiStatus.UNFAVORITE_SUCCESS.response());
    }
}