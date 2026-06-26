package org.example.controller;

import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.example.model.dto.CommentCommand;
import org.example.model.dto.CommentVosDto;
import org.example.model.enums.CommentApiStatus;
import org.example.service.CommentService;
import org.example.util.SecurityUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 评论控制器
 * 提供评论相关的 RESTful API 接口
 */
@Log4j2
@RestController
@RequestMapping("/api/comment")
public class CommentController {

    @Resource
    private CommentService commentService;

    /**
     * 添加评论
     * POST /api/comments
     */
    @PostMapping
    public ResponseEntity<?> addComment(@RequestBody CommentCommand command) {
        commentService.addComment(command);
        return ResponseEntity.status(201)
                .body(CommentApiStatus.ADD_SUCCESS.response());

    }

    /**
     * 删除评论（软删除）
     * DELETE /api/comments/{commentId}
     */
    @DeleteMapping("/{commentId}")
    @PreAuthorize("hasAnyRole('USER','ADMIN','OFFICER')")
    public ResponseEntity<?> deleteComment(
            @PathVariable String commentId) {
        Long userId = SecurityUtils.getCurrentUserId();
        commentService.deleteComment(commentId, userId);
        return ResponseEntity.status(204)
                .body(CommentApiStatus.DELETED_SUCCESS.response());
    }

    /**
     * 获取某帖子的评论（按热度排序，游标分页）
     * GET /api/comments/post/{postId}/hot
     */
    @GetMapping("/post/{postId}/hot")
    public ResponseEntity<?> getCommentsByResourceByHot(
            @PathVariable Long postId,
            @RequestParam(required = false) Integer cursorLikeCount,
            @RequestParam(required = false) String cursorId,
            @RequestParam(defaultValue = "20") int limit) {
        log.info("获取资源评论(按热度): resourceId={}, cursorLikeCount={}, cursorId={}, limit={}",
                 postId, cursorLikeCount, cursorId, limit);
        CommentVosDto dto = commentService.getRootCommentsByPostByHot(postId, cursorLikeCount, cursorId, limit);
        return ResponseEntity.ok().body(CommentApiStatus.GET_SUCCESS.response(dto));
    }

    /**
     * 获取某评论的所有回复
     * GET /api/comments/replies/{commentId}
     */
    @GetMapping("/replies/{commentId}")
    public ResponseEntity<?> getReplies(
            @PathVariable String commentId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit) {
        log.info("获取评论回复: commentId={}, page={}, limit={}", commentId, page, limit);
        CommentVosDto dto = commentService.getReplies(commentId, page, limit);
        return ResponseEntity.ok().body(CommentApiStatus.GET_SUCCESS.response(dto));
    }

    /**
     * 点赞评论
     * POST /api/comment/{commentId}/like
     */
    @PostMapping("/{commentId}/like")
    public ResponseEntity<?> likeComment(@PathVariable String commentId) {
        Long userId = SecurityUtils.getCurrentUserId();
        log.info("点赞评论: commentId={}, userId={}", commentId, userId);
        commentService.likeComment(commentId, userId);
        return ResponseEntity.ok().body(CommentApiStatus.LIKE_SUCCESS.response());
    }

    /**
     * 取消点赞评论
     * DELETE /api/comment/{commentId}/like
     */
    @DeleteMapping("/{commentId}/like")
    public ResponseEntity<?> unlikeComment(@PathVariable String commentId) {
        Long userId = SecurityUtils.getCurrentUserId();
        log.info("取消点赞评论: commentId={}, userId={}", commentId, userId);
        commentService.unlikeComment(commentId, userId);
        return ResponseEntity.ok().body(CommentApiStatus.UNLIKE_SUCCESS.response());
    }

    /**
     * 健康检查接口
     * GET /api/comments/health
     */
    @GetMapping("/health")
    public Map<String, Object> healthCheck() {
        return Map.of(
                "status", "healthy",
                "message", "评论服务运行正常"
        );
    }
}
