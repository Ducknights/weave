package org.example.service.impl;

import lombok.extern.log4j.Log4j2;
import org.example.dto.UserBriefDto;
import org.example.feign.UserInfoFeign;
import org.example.model.dto.CommentCommand;
import org.example.model.dto.CommentDto;
import org.example.model.dto.CommentPageDto;
import org.example.model.dto.PaginationDto;
import org.example.model.entity.Comment;
import org.example.model.enums.SortEnum;
import org.example.repository.CommentRepository;
import org.example.service.CommentService;
import org.bson.types.ObjectId;
import org.example.util.SecurityUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 评论服务实现
 * 提供评论相关的业务逻辑实现
 */
@Log4j2
@Service
public class CommentServiceImpl implements CommentService {
    
    @Resource
    private CommentRepository commentRepository;
    
    @Resource
    private UserInfoFeign userInfoFeign;
    
    @Resource
    private MongoTemplate mongoTemplate;

    @Override
    public void addComment(CommentCommand command) {
        Long userId = SecurityUtils.getCurrentUserId();
        
        // 获取用户基本信息
        UserBriefDto userDto = null;
        try {
            userDto = userInfoFeign.getUserInfosById(userId);
        } catch (Exception e) {
            log.warn("获取用户信息失败:", e);
        }
        
        // 用户信息为空抛出异常
        if (userDto == null) {
            throw new RuntimeException("用户信息获取失败");
        }
        
        // 构建文档
        Comment comment = Comment.builder()
                .resourceId(command.resourceId())
                .parentId(command.parentId())
                .userId(userId)
                .userName(userDto.getName())
                .avatar(userDto.getAvatar())
                .content(command.content())
                .build();
        
        // 验证评论数据
        Comment.ValidationResult validation = Comment.validate(comment);
        if (!validation.isValid()) {
            throw new RuntimeException(String.join(", ", validation.getErrors()));
        }
        
        // 判断是否重复评论
        Comment existingComment = commentRepository.findDuplicateComment(
                comment.getResourceId(),
                String.valueOf(userId),
                comment.getParentId(),
                comment.getContent(),
                Comment.STATUS_DELETED
        );
        
        if (existingComment != null) {
            throw new RuntimeException("重复评论");
        }
        
        // 根评论
        if (comment.getParentId() == null || comment.getParentId().isEmpty()) {
            commentRepository.save(comment);
        } 
        // 子评论
        else {
            // 验证父评论ID格式
            if (!ObjectId.isValid(comment.getParentId())) {
                throw new RuntimeException("无效评论");
            }
            
            // 查询父评论是否存在
            ObjectId parentObjectId = new ObjectId(comment.getParentId());
            Optional<Comment> parentCommentOptional = commentRepository.findById(parentObjectId);
            Comment parentComment = parentCommentOptional.orElse(null);
            
            if (parentComment == null || parentComment.getStatus() == Comment.STATUS_DELETED) {
                throw new RuntimeException("父评论不存在");
            }
            
            // 插入子评论
            commentRepository.save(comment);
            
            // 更新父评论的回复数
            parentComment.setReplyCount(parentComment.getReplyCount() + 1);
            commentRepository.save(parentComment);
        }
    }

    @Override
    public Comment getCommentById(String commentId) {
        // 验证ObjectId格式
        if (!ObjectId.isValid(commentId)) {
            throw new RuntimeException("无效的评论ID格式");
        }
        
        ObjectId commentObjectId = new ObjectId(commentId);
        Optional<Comment> commentOptional = commentRepository.findById(commentObjectId);
        Comment comment = commentOptional.orElse(null);
        
        if (comment == null || comment.getStatus() != Comment.STATUS_VISIBLE) {
            throw new RuntimeException("评论不存在");
        }
        
        return comment;
    }

    /**
     * 根据资源ID和时间分页获取评论
     *
     * @param resourceId 资源ID
     * @param cursorTime 游标时间
     * @param cursorId 游标ID
     * @param limit 限制
     * @return 评论分页DTO
     */
    @Override
    public CommentPageDto getCommentsByResourceByTime(String resourceId, String cursorTime, String cursorId, int limit) {
        List<AggregationOperation> operations = new ArrayList<>();

        operations.add(Aggregation.match(
                Criteria.where("resourceId").is(resourceId)
                        .and("status").is(Comment.STATUS_VISIBLE)
                        .and("parentId").in(null, "")
        ));

        if (cursorTime != null && !cursorTime.isEmpty()) {
            try {
                LocalDateTime cursorDateTime = LocalDateTime.parse(cursorTime);
                if (cursorId != null && ObjectId.isValid(cursorId)) {
                    ObjectId cursorObjectId = new ObjectId(cursorId);
                    operations.add(Aggregation.match(
                            new Criteria().orOperator(
                                    Criteria.where("createdTime").lt(cursorDateTime),
                                    Criteria.where("createdTime").is(cursorDateTime)
                                            .and("_id").lt(cursorObjectId)
                            )
                    ));
                } else {
                    operations.add(Aggregation.match(
                            Criteria.where("createdTime").lt(cursorDateTime)
                    ));
                }
            } catch (Exception e) {
                log.warn("无效的游标时间格式: {}", cursorTime);
            }
        }
        operations.add(Aggregation.sort(Sort.Direction.DESC, "createdTime", "_id"));

        operations.add(Aggregation.limit(limit));

        Aggregation aggregation = Aggregation.newAggregation(operations);
        AggregationResults<Comment> results = mongoTemplate.aggregate(aggregation, "comments", Comment.class);
        List<Comment> comments = results.getMappedResults();

        boolean hasMore = comments.size() == limit;

        CommentDto commentDto = CommentDto.builder()
                .comments(comments)
                .build();

        PaginationDto paginationDto = PaginationDto.builder()
                .page(1)
                .limit(limit)
                .hasMore(hasMore)
                .sortBy("time")
                .nextCursorTime(hasMore && !comments.isEmpty() ? comments.get(comments.size() - 1).getCreatedTime().toString() : null)
                .nextCursorId(hasMore && !comments.isEmpty() ? comments.get(comments.size() - 1).getId().toString() : null)
                .build();

        return new CommentPageDto(commentDto, paginationDto);
    }

    /**
     * 根据资源ID和热度分页获取评论
     *
     * @param resourceId 资源ID
     * @param cursorLikeCount 游标点赞数
     * @param cursorReplyCount 游标回复数
     * @param cursorId 游标ID
     * @param limit 限制
     * @return 评论分页DTO
     */
    @Override
    public CommentPageDto getCommentsByResourceByHot(String resourceId, Integer cursorLikeCount, Integer cursorReplyCount, String cursorId, int limit) {
        List<AggregationOperation> operations = new ArrayList<>();

        operations.add(Aggregation.match(
                Criteria.where("resourceId").is(resourceId)
                        .and("status").is(Comment.STATUS_VISIBLE)
                        .and("parentId").in(null, "")
        ));

        if (cursorLikeCount != null) {
            if (cursorReplyCount != null && cursorId != null && ObjectId.isValid(cursorId)) {
                ObjectId cursorObjectId = new ObjectId(cursorId);
                operations.add(Aggregation.match(
                        new Criteria().orOperator(
                                Criteria.where("likeCount").lt(cursorLikeCount),
                                Criteria.where("likeCount").is(cursorLikeCount)
                                        .and("replyCount").lt(cursorReplyCount),
                                Criteria.where("likeCount").is(cursorLikeCount)
                                        .and("replyCount").is(cursorReplyCount)
                                        .and("_id").lt(cursorObjectId)
                        )
                ));
            } else if (cursorReplyCount != null) {
                operations.add(Aggregation.match(
                        new Criteria().orOperator(
                                Criteria.where("likeCount").lt(cursorLikeCount),
                                Criteria.where("likeCount").is(cursorLikeCount)
                                        .and("replyCount").lt(cursorReplyCount)
                        )
                ));
            } else {
                operations.add(Aggregation.match(
                        Criteria.where("likeCount").lt(cursorLikeCount)
                ));
            }
        }
        operations.add(Aggregation.sort(Sort.Direction.DESC, "likeCount", "replyCount", "_id"));

        operations.add(Aggregation.limit(limit));

        Aggregation aggregation = Aggregation.newAggregation(operations);
        AggregationResults<Comment> results = mongoTemplate.aggregate(aggregation, "comments", Comment.class);
        List<Comment> comments = results.getMappedResults();

        boolean hasMore = comments.size() == limit;

        CommentDto commentDto = CommentDto.builder()
                .comments(comments)
                .build();

        PaginationDto paginationDto = PaginationDto.builder()
                .page(1)
                .limit(limit)
                .hasMore(hasMore)
                .sortBy("hot")
                .nextCursorId(hasMore && !comments.isEmpty() ? comments.get(comments.size() - 1).getId().toString() : null)
                .nextCursorLikeCount(hasMore && !comments.isEmpty() ? comments.get(comments.size() - 1).getLikeCount() : null)
                .nextCursorReplyCount(hasMore && !comments.isEmpty() ? comments.get(comments.size() - 1).getReplyCount() : null)
                .build();
        return new CommentPageDto(commentDto, paginationDto);

    }

    @Override
    public Map<String, Object> getCommentsByUser(String userId, int page, int limit) {
        try {
            List<Comment> comments = commentRepository.findByUserId(userId, Comment.STATUS_VISIBLE);
            
            // 分页处理
            int start = (page - 1) * limit;
            int end = Math.min(start + limit, comments.size());
            List<Comment> paginatedComments = comments.subList(
                    start, 
                    end
            );
            
            // 计算总数
            long total = comments.size();
            
            Map<String, Object> pagination = Map.of(
                    "total", total,
                    "page", page,
                    "limit", limit,
                    "totalPages", (int) Math.ceil((double) total / limit)
            );
            
            return Map.of(
                    "code", 200,
                    "message", "获取数据成功",
                    "data", Map.of(
                            "comments", paginatedComments,
                            "pagination", pagination
                    )
            );
        } catch (Exception e) {
            log.error("获取用户评论失败:", e);
            return Map.of(
                    "code", 500,
                    "message", "获取数据失败",
                    "data", null
            );
        }
    }

    @Override
    public Map<String, Object> getReplies(String commentId, int page, int limit) {
        try {
            // 验证ObjectId格式
            if (!ObjectId.isValid(commentId)) {
                return Map.of(
                        "code", 400,
                        "message", "无效的评论ID格式",
                        "data", null
                );
            }
            
            List<Comment> replies = commentRepository.findByParentId(commentId, Comment.STATUS_VISIBLE);
            
            // 分页处理
            int start = (page - 1) * limit;
            int end = Math.min(start + limit, replies.size());
            List<Comment> paginatedReplies = replies.subList(
                    start, 
                    end
            );
            
            // 计算总数
            long total = replies.size();
            
            Map<String, Object> pagination = Map.of(
                    "total", total,
                    "page", page,
                    "limit", limit,
                    "totalPages", (int) Math.ceil((double) total / limit)
            );
            
            return Map.of(
                    "code", 200,
                    "message", "获取回复成功",
                    "data", Map.of(
                            "replies", paginatedReplies,
                            "pagination", pagination
                    )
            );
        } catch (Exception e) {
            log.error("获取回复失败:", e);
            return Map.of(
                    "code", 500,
                    "message", "获取数据失败",
                    "data", null
            );
        }
    }

    @Override
    public Map<String, Object> toggleLike(String commentId, String userId) {
        try {
            // 验证ObjectId格式
            if (!ObjectId.isValid(commentId)) {
                return Map.of(
                        "code", 400,
                        "message", "无效的评论ID格式",
                        "data", null
                );
            }
            
            if (userId == null || userId.isEmpty()) {
                return Map.of(
                        "code", 400,
                        "message", "缺少用户ID",
                        "data", null
                );
            }
            
            ObjectId commentObjectId = new ObjectId(commentId);
            Optional<Comment> commentOptional = commentRepository.findById(commentObjectId);
            Comment comment = commentOptional.orElse(null);
            
            if (comment == null || comment.getStatus() != Comment.STATUS_VISIBLE) {
                return Map.of(
                        "code", 404,
                        "message", "评论不存在",
                        "data", null
                );
            }
            
            List<String> likedUsers = comment.getLikedUsers();
            if (likedUsers == null) {
                likedUsers = new ArrayList<>();
                comment.setLikedUsers(likedUsers);
            }
            
            boolean isLiked = likedUsers.contains(userId);
            
            if (isLiked) {
                // 取消点赞
                likedUsers.remove(userId);
                comment.setLikeCount(comment.getLikeCount() - 1);
                commentRepository.save(comment);
                return Map.of(
                        "code", 200,
                        "message", "取消点赞成功",
                        "data", Map.of("isLiked", false)
                );
            } else {
                // 添加点赞
                likedUsers.add(userId);
                comment.setLikeCount(comment.getLikeCount() + 1);
                commentRepository.save(comment);
                return Map.of(
                        "code", 200,
                        "message", "点赞成功",
                        "data", Map.of("isLiked", true)
                );
            }
        } catch (Exception e) {
            log.error("点赞操作失败:", e);
            return Map.of(
                    "code", 500,
                    "message", "操作失败",
                    "data", null
            );
        }
    }

    @Override
    public Map<String, Object> updateComment(String commentId, String content, String userId) {
        try {
            // 验证ObjectId格式
            if (!ObjectId.isValid(commentId)) {
                return Map.of(
                        "code", 400,
                        "message", "无效的评论ID格式",
                        "data", null
                );
            }
            
            if (content == null || content.isEmpty() || userId == null || userId.isEmpty()) {
                return Map.of(
                        "code", 400,
                        "message", "缺少必要参数",
                        "data", null
                );
            }
            
            ObjectId commentObjectId = new ObjectId(commentId);
            Optional<Comment> commentOptional = commentRepository.findById(commentObjectId);
            Comment comment = commentOptional.orElse(null);
            
            if (comment == null) {
                return Map.of(
                        "code", 404,
                        "message", "评论不存在",
                        "data", null
                );
            }
            
            // 检查是否是评论作者
            if (!comment.getUserId().equals(userId)) {
                return Map.of(
                        "code", 403,
                        "message", "无权编辑此评论",
                        "data", null
                );
            }
            
            // 检查评论是否已删除
            if (comment.getStatus() == Comment.STATUS_DELETED) {
                return Map.of(
                        "code", 400,
                        "message", "已删除的评论无法编辑",
                        "data", null
                );
            }
            
            // 更新评论
            comment.setContent(content);
            comment.setUpdatedTime(LocalDateTime.now());
            Comment updatedComment = commentRepository.save(comment);
            
            return Map.of(
                    "code", 200,
                    "message", "编辑成功",
                    "data", updatedComment
            );
        } catch (Exception e) {
            log.error("编辑评论失败:", e);
            return Map.of(
                    "code", 500,
                    "message", "编辑失败",
                    "data", null
            );
        }
    }

    @Override
    public Map<String, Object> deleteComment(String commentId, String userId, boolean isAdmin) {
        try {
            // 验证ObjectId格式
            if (!ObjectId.isValid(commentId)) {
                return Map.of(
                        "code", 400,
                        "message", "无效的评论ID格式",
                        "data", null
                );
            }
            
            if (userId == null || userId.isEmpty()) {
                return Map.of(
                        "code", 400,
                        "message", "缺少用户ID",
                        "data", null
                );
            }
            
            ObjectId commentObjectId = new ObjectId(commentId);
            Optional<Comment> commentOptional = commentRepository.findById(commentObjectId);
            Comment comment = commentOptional.orElse(null);
            
            if (comment == null) {
                return Map.of(
                        "code", 404,
                        "message", "评论不存在",
                        "data", null
                );
            }
            
            // 检查权限（作者或管理员）
            if (!comment.getUserId().equals(userId) && !isAdmin) {
                return Map.of(
                        "code", 403,
                        "message", "无权删除此评论",
                        "data", null
                );
            }
            
            // 软删除评论
            comment.setStatus(Comment.STATUS_DELETED);
            comment.setUpdatedTime(LocalDateTime.now());
            commentRepository.save(comment);
            
            // 如果是子评论，更新父评论的回复数
            if (comment.getParentId() != null && !comment.getParentId().isEmpty()) {
                if (ObjectId.isValid(comment.getParentId())) {
                    ObjectId parentObjectId = new ObjectId(comment.getParentId());
                    Optional<Comment> parentCommentOptional = commentRepository.findById(parentObjectId);
                    Comment parentComment = parentCommentOptional.orElse(null);
                    if (parentComment != null && parentComment.getReplyCount() > 0) {
                        parentComment.setReplyCount(parentComment.getReplyCount() - 1);
                        commentRepository.save(parentComment);
                    }
                }
            }
            
            return Map.of(
                    "code", 200,
                    "message", "删除成功",
                    "data", null
            );
        } catch (Exception e) {
            log.error("删除评论失败:", e);
            return Map.of(
                    "code", 500,
                    "message", "删除失败",
                    "data", null
            );
        }
    }
}
