package org.example.repository;

import org.bson.types.ObjectId;
import org.example.model.entity.CommentLike;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * 评论点赞记录仓库
 */
@Repository
public interface CommentLikeRepository extends MongoRepository<CommentLike, ObjectId> {

    boolean existsByCommentIdAndUserId(ObjectId commentId, Long userId);

    void deleteByCommentIdAndUserId(ObjectId commentId, Long userId);
}
