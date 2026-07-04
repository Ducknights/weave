package com.weave.comment.model.entity;

import lombok.Builder;
import lombok.Data;
import lombok.Builder.Default;
import org.bson.types.ObjectId;
import com.weave.comment.exception.BusinessException;
import com.weave.comment.model.enums.CommentApiStatus;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * 评论实体类
 * 映射到 MongoDB 的 comments 集合
 */
@Builder
@Data
@Document(collection = "comments")
@CompoundIndexes({
        @CompoundIndex(
                name = "idx_post_status_parent_like_id",
                def = "{'postId': 1, 'status': 1, 'parentId': 1, 'likeCount': -1, '_id': -1}"
        ),
        @CompoundIndex(
                name = "idx_post_status_parent_time_id",
                def = "{'postId': 1, 'status': 1, 'parentId': 1, 'createdTime': -1, '_id': -1}"
        ),
        @CompoundIndex(
                name = "idx_parent_status_time",
                def = "{'parentId': 1, 'status': 1, 'createdTime': 1}"
        )
})
public class Comment {
    private ObjectId id;
    private Long postId;  // 帖子ID
    private String parentId;    // 父评论ID
    private Long userId;    // 用户ID
    private String content;    // 评论内容
    
    @Default
    private int replyCount = 0;   // 回复数量
    
    @Default
    private int likeCount = 0;    // 点赞数量
    
    @Default
    private LocalDateTime createdTime = LocalDateTime.now();
    
    @Default
    private int status = STATUS_VISIBLE;
    
    // 评论状态常量
    public static final int STATUS_VISIBLE = 1; // 可见
    public static final int STATUS_DELETED = 2; // 删除

    public static final int CONTENT_LENGTH = 200; // 评论内容长度限制

    /**
     * 验证评论数据
     */
    public static void validate(Comment comment) {
        // 验证资源ID
        if (comment.getPostId() == null) {
            throw new BusinessException(CommentApiStatus.MISSING_POST_ID);
        }
        // 验证用户ID
        if (comment.getUserId() == null) {
            throw new BusinessException(CommentApiStatus.MISSING_USER_ID);
        }
        // 验证评论内容
        if (comment.getContent() == null || comment.getContent().trim().isEmpty()) {
            throw new BusinessException(CommentApiStatus.EMPTY_CONTENT);
        } else if (comment.getContent().length() > CONTENT_LENGTH) {
            throw new BusinessException(CommentApiStatus.CONTENT_TOO_LONG);
        }
    }
}
