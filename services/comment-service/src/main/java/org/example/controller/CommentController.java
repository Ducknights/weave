package org.example.controller;

import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.example.model.ApiResult;
import org.example.model.dto.CommentCommand;
import org.example.model.dto.CommentPageDto;
import org.example.model.entity.Comment;
import org.example.model.enums.CommentApiStatus;
import org.example.service.CommentService;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ApiResult<?>> addComment(@RequestBody CommentCommand command) {
        commentService.addComment(command);
        return ResponseEntity
                .status(201)
                .body(CommentApiStatus.ADD_SUCCESS.response());

    }

    /**
     * 获取评论详情
     * GET /api/comments/{commentId}
     */
    @GetMapping("/{commentId}")
    public ResponseEntity<ApiResult<?>> getCommentById(@PathVariable String commentId) {
        log.info("获取评论详情: commentId={}", commentId);
        Comment comment = commentService.getCommentById(commentId);
        return ResponseEntity
                .ok()
                .body(CommentApiStatus.GET_SUCCESS.response(comment));
    }

    /**
     * 获取某资源的所有评论（按时间排序，游标分页）
     * GET /api/comments/resource/{resourceId}/time
     */
    @GetMapping("/resource/{resourceId}/time")
    public CommentPageDto getCommentsByResourceByTime(
            @PathVariable String resourceId,
            @RequestParam(required = false) String cursorTime,
            @RequestParam(required = false) String cursorId,
            @RequestParam(defaultValue = "20") int limit) {
        log.info("获取资源评论(按时间): resourceId={}, cursorTime={}, cursorId={}, limit={}", 
                 resourceId, cursorTime, cursorId, limit);
        return commentService.getCommentsByResourceByTime(resourceId, cursorTime, cursorId, limit);
    }

    /**
     * 获取某资源的所有评论（按热度排序，游标分页）
     * GET /api/comments/resource/{resourceId}/hot
     */
    @GetMapping("/resource/{resourceId}/hot")
    public CommentPageDto getCommentsByResourceByHot(
            @PathVariable String resourceId,
            @RequestParam(required = false) Integer cursorLikeCount,
            @RequestParam(required = false) Integer cursorReplyCount,
            @RequestParam(required = false) String cursorId,
            @RequestParam(defaultValue = "20") int limit) {
        log.info("获取资源评论(按热度): resourceId={}, cursorLikeCount={}, cursorReplyCount={}, cursorId={}, limit={}", 
                 resourceId, cursorLikeCount, cursorReplyCount, cursorId, limit);
        return commentService.getCommentsByResourceByHot(resourceId, cursorLikeCount, cursorReplyCount, cursorId, limit);
    }

    /**
     * 获取某用户的所有评论
     * GET /api/comments/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public Map<String, Object> getCommentsByUser(
            @PathVariable String userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit) {
        log.info("获取用户评论: userId={}, page={}, limit={}", userId, page, limit);
        return commentService.getCommentsByUser(userId, page, limit);
    }

    /**
     * 获取某评论的所有回复
     * GET /api/comments/replies/{commentId}
     */
    @GetMapping("/replies/{commentId}")
    public Map<String, Object> getReplies(
            @PathVariable String commentId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit) {
        log.info("获取评论回复: commentId={}, page={}, limit={}", commentId, page, limit);
        return commentService.getReplies(commentId, page, limit);
    }

    /**
     * 点赞/取消点赞评论
     * POST /api/comments/{commentId}/like
     */
    @PostMapping("/{commentId}/like")
    public Map<String, Object> toggleLike(
            @PathVariable String commentId,
            @RequestBody Map<String, String> request) {
        String userId = request.get("userId");
        log.info("点赞操作: commentId={}, userId={}", commentId, userId);
        return commentService.toggleLike(commentId, userId);
    }

    /**
     * 编辑评论
     * PUT /api/comments/{commentId}
     */
    @PutMapping("/{commentId}")
    public Map<String, Object> updateComment(
            @PathVariable String commentId,
            @RequestBody Map<String, String> request) {
        String content = request.get("content");
        String userId = request.get("userId");
        log.info("编辑评论: commentId={}, userId={}", commentId, userId);
        return commentService.updateComment(commentId, content, userId);
    }

    /**
     * 删除评论（软删除）
     * DELETE /api/comments/{commentId}
     */
    @DeleteMapping("/{commentId}")
    public Map<String, Object> deleteComment(
            @PathVariable String commentId,
            @RequestBody Map<String, Object> request) {
        String userId = (String) request.get("userId");
        boolean isAdmin = request.get("isAdmin") != null && (boolean) request.get("isAdmin");
        log.info("删除评论: commentId={}, userId={}, isAdmin={}", commentId, userId, isAdmin);
        return commentService.deleteComment(commentId, userId, isAdmin);
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
