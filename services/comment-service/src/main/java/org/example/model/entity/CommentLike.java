package org.example.model.entity;

import lombok.Builder;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * 评论点赞记录实体
 * 映射到 MongoDB 的 comment_likes 集合
 */
@Builder
@Data
@Document(collection = "comment_likes")
@CompoundIndex(def = "{'commentId': 1, 'userId': 1}", unique = true)
public class CommentLike {
    private ObjectId id;
    private ObjectId commentId;   // 被点赞的评论ID
    private Long userId;           // 点赞用户ID
    private LocalDateTime createdTime;  // 点赞时间
}
