package org.example.service;

import org.example.model.dto.CommentCommand;
import org.example.model.dto.CommentPageDto;
import org.example.model.entity.Comment;

import java.util.Map;

/**
 * 评论服务接口
 * 提供评论相关的业务逻辑
 */
public interface CommentService {
    
    /**
     * 添加评论
     */
    void addComment(CommentCommand command);
    
    /**
     * 获取评论详情
     */
     Comment getCommentById(String commentId);

     /**
     * 获取资源的评论（按时间排序）
     * @param resourceId 资源ID
     * @param cursorTime 游标时间
     * @param cursorId 游标ID
     * @param limit 每页大小
     */
    CommentPageDto getCommentsByResourceByTime(String resourceId, String cursorTime, String cursorId, int limit);
    
    /**
     * 获取资源的评论（按热度排序）
     * @param resourceId 资源ID
     * @param cursorLikeCount 游标点赞数
     * @param cursorReplyCount 游标回复数
     * @param cursorId 游标ID
     * @param limit 每页大小
     */
    CommentPageDto getCommentsByResourceByHot(String resourceId, Integer cursorLikeCount, Integer cursorReplyCount, String cursorId, int limit);
    
    /**
     * 获取某用户的所有评论
     */
    Map<String, Object> getCommentsByUser(String userId, int page, int limit);
    
    /**
     * 获取某评论的所有回复
     */
    Map<String, Object> getReplies(String commentId, int page, int limit);
    
    /**
     * 点赞/取消点赞评论
     */
    Map<String, Object> toggleLike(String commentId, String userId);
    
    /**
     * 编辑评论
     */
    Map<String, Object> updateComment(String commentId, String content, String userId);
    
    /**
     * 删除评论（软删除）
     */
    Map<String, Object> deleteComment(String commentId, String userId, boolean isAdmin);
}
