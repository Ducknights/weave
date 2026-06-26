package org.example.service;

import org.example.model.dto.CommentCommand;
import org.example.model.dto.CommentVosDto;

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
     * 获取资源的评论（按热度排序）
     *
     * @param postId          资源ID
     * @param cursorLikeCount 游标点赞数
     * @param cursorId        游标ID
     * @param limit           每页大小
     */
    CommentVosDto getRootCommentsByPostByHot(Long postId, Integer cursorLikeCount, String cursorId, int limit);

    /**
     * 获取某评论的回复
     * @param commentId 评论ID
     * @param page    页码
     * @param limit   每页大小
     * @return 回复列表
     */
    CommentVosDto getReplies(String commentId, int page, int limit);

    /**
     * 点赞评论
     */
    void likeComment(String commentId, Long userId);

    /**
     * 取消点赞评论
     */
    void unlikeComment(String commentId, Long userId);

    /**
     * 删除评论（软删除）
     */
    void deleteComment(String commentId, Long userId);
}
