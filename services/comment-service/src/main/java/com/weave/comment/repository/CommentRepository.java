package com.weave.comment.repository;

import com.weave.comment.model.entity.Comment;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * 评论仓库
 * 用于操作 MongoDB 中的评论集合
 */
@Repository
public interface CommentRepository extends MongoRepository<Comment, ObjectId> {
    /**
     * 根据父评论ID查询回复
     */
    @Query(value = "{ 'parentId': ?0, 'status': ?1 }")
    Page<Comment> findByParentId(String parentId, int status, Pageable pageable);

    /**
     * 检查是否存在重复评论
     */
    @Query(value = "{ 'postId': ?0, 'userId': ?1, 'parentId': ?2, 'content': ?3, 'status': { $ne: ?4 } }",exists = true)
    boolean existsDuplicateComment(Long postId, Long userId, String parentId, String content, int status);
}
