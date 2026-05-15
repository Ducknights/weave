package org.example.repository;

import org.bson.types.ObjectId;
import org.example.model.entity.Comment;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 评论仓库
 * 用于操作 MongoDB 中的评论集合
 */
@Repository
public interface CommentRepository extends MongoRepository<Comment, ObjectId> {
    
    /**
     * 根据资源ID和游标查询评论（游标分页）
     * @param resourceId 资源ID
     * @param status 状态排除条件
     * @param cursorTime 游标时间戳（用于分页）
     * @param cursorId 游标ID（用于处理同一时间的多条记录）
     * @param limit 返回数量限制
     * @param sort 排序方式
     */
    @Query(value = "{ 'resourceId': ?0, 'status': { $ne: ?1 }, " +
                   "$or: [ { 'createdTime': { $lt: ?2 } }, " +
                          "{ 'createdTime': ?2, '_id': { $lt: ?3 } } ] }")
    List<Comment> findByResourceIdWithCursor(String resourceId, int status, 
                                            LocalDateTime cursorTime, ObjectId cursorId,
                                            int limit, Sort sort);
    
    /**
     * 根据资源ID查询根评论（按时间排序，游标分页）
     */
    @Query(value = "{ 'resourceId': ?0, 'parentId': { $in: [null, ''] }, 'status': { $ne: ?1 }, " +
                   "$or: [ { 'createdTime': { $lt: ?2 } }, " +
                          "{ 'createdTime': ?2, '_id': { $lt: ?3 } } ] }")
    List<Comment> findRootCommentsByResourceIdWithCursor(String resourceId, int status,
                                                         LocalDateTime cursorTime, ObjectId cursorId,
                                                         int limit, Sort sort);
    
    /**
     * 根据资源ID查询根评论（按热度排序，游标分页）
     * 使用 likeCount 作为热度指标
     */
    @Query(value = "{ 'resourceId': ?0, 'parentId': { $in: [null, ''] }, 'status': { $ne: ?1 }, " +
                   "$or: [ { 'likeCount': { $lt: ?2 } }, " +
                          "{ 'likeCount': ?2, 'replyCount': { $lt: ?3 } }, " +
                          "{ 'likeCount': ?2, 'replyCount': ?3, '_id': { $lt: ?4 } } ] }")
    List<Comment> findRootCommentsByResourceIdWithCursorByHot(String resourceId, int status,
                                                              int cursorLikeCount, ObjectId cursorId,
                                                              int limit, Sort sort);
    
    /**
     * 统计资源的根评论数量
     */
    @Query(value = "{ 'resourceId': ?0, 'parentId': { $in: [null, ''] }, 'status': { $ne: ?1 } }", count = true)
    long countRootCommentsByResourceId(String resourceId, int status);
    
    /**
     * 根据用户ID查询评论
     *
     * @Query 注解用于自定义MongoDB查询语句
     * 查询条件为：匹配指定userId且status不等于指定值的文档
     *
     * @param userId 用户ID，作为查询条件的第一部分
     * @param status 状态值，查询将排除status等于此值的文档
     * @return 返回符合条件的Comment对象列表
     */
    @Query(value = "{ 'userId': ?0, 'status': { $ne: ?1 } }")
    List<Comment> findByUserId(String userId, int status);
    
    /**
     * 根据父评论ID查询回复
     */
    @Query(value = "{ 'parentId': ?0, 'status': ?1 }")
    List<Comment> findByParentId(String parentId, int status);
    
    /**
     * 根据父评论ID和游标查询回复（游标分页）
     */
    @Query(value = "{ 'parentId': ?0, 'status': ?1, " +
                   "$or: [ { 'createdTime': { $lt: ?2 } }, " +
                          "{ 'createdTime': ?2, '_id': { $lt: ?3 } } ] }")
    List<Comment> findRepliesWithCursor(String parentId, int status,
                                       LocalDateTime cursorTime, ObjectId cursorId,
                                       int limit, Sort sort);
    
    /**
     * 检查是否存在重复评论
     */
    @Query(value = "{ 'resourceId': ?0, 'userId': ?1, 'parentId': ?2, 'content': ?3, 'status': { $ne: ?4 } }")
    Comment findDuplicateComment(String resourceId, String userId, String parentId, String content, int status);
    
    /**
     * 根据父评论ID更新回复数
     */
    @Query(value = "{ 'id': ?0 }")
    void updateReplyCount(ObjectId parentId, int delta);
    
    /**
     * 查询资源的根评论（按热度排序，不带游标，用于首次查询）
     */
    @Query(value = "{ 'resourceId': ?0, 'parentId': { $in: [null, ''] }, 'status': { $ne: ?1 } }")
    List<Comment> findRootCommentsByResourceIdOrderByHot(String resourceId, int status, Sort sort);
}
