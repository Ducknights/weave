package org.example.service.impl;

import lombok.extern.log4j.Log4j2;
import org.example.dto.UserBriefDto;
import org.example.exception.BusinessException;
import org.example.feign.UserFeignClient;
import org.example.model.dto.CommentCommand;
import org.example.model.dto.CommentVosDto;
import org.example.model.entity.Comment;
import org.example.model.enums.CommentApiStatus;
import org.example.model.vo.CommentVo;
import org.example.repository.CommentLikeRepository;
import org.example.repository.CommentRepository;
import org.example.service.CommentService;
import org.bson.types.ObjectId;
import org.example.model.entity.CommentLike;
import org.example.util.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
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
    private CommentLikeRepository commentLikeRepository;
    @Resource
    private MongoTemplate mongoTemplate;
    @Resource
    private UserFeignClient userFeignClient;

    @Override
    public void addComment(CommentCommand command) {
        // 获取当前用户ID
        Long userId = SecurityUtils.getCurrentUserId();

        // 构建文档
        Comment comment = Comment.builder()
                .postId(command.postId())
                .parentId(command.parentId())
                .userId(userId)
                .content(command.content())
                .build();

        // 验证评论数据
        Comment.validate(comment);

        // 判断是否重复评论
        if (commentRepository.existsDuplicateComment(
                comment.getPostId(),
                comment.getUserId(),
                comment.getParentId(),
                comment.getContent(),
                Comment.STATUS_VISIBLE
        )){
            throw new BusinessException(CommentApiStatus.DUPLICATE_COMMENT);
        }

        // 根评论
        if (comment.getParentId() == null || comment.getParentId().isEmpty()) {
            commentRepository.save(comment);
        }
        // 子评论
        else {
            // 验证父评论ID格式
            if (!ObjectId.isValid(comment.getParentId())) {
                throw new BusinessException(CommentApiStatus.INVALID_PARENT_ID);
            }

            // 查询父评论是否存在，或者找到了但状态是已删除，则抛出异常
            Comment parentComment = commentRepository.findById(new ObjectId(comment.getParentId()))
                    .filter(c -> c.getStatus() != Comment.STATUS_DELETED)
                    .orElseThrow(() -> new BusinessException(CommentApiStatus.COMMENT_NOT_FOUND));

            // 插入子评论
            commentRepository.save(comment);

            // 更新父评论的回复数
            parentComment.setReplyCount(parentComment.getReplyCount() + 1);
            commentRepository.save(parentComment);
        }
    }

    /**
     * 根据资源ID和热度分页获取评论
     *
     * @param postId 资源ID
     * @param cursorLikeCount 游标点赞数
     * @param cursorId 游标ID
     * @param limit 限制
     * @return 评论分页DTO
     */
    @Override
    public CommentVosDto getRootCommentsByPostByHot(Long postId, Integer cursorLikeCount, String cursorId, int limit) {
        // 构建查询条件
        Query query = new Query();

        // 过滤条件
        query.addCriteria(Criteria.where("postId").is(postId)
                .and("status").is(Comment.STATUS_VISIBLE)
                .and("parentId").in(null, "")); // 根评论

        // 添加过滤条件（likeCount < cursorLikeCount, _id < cursorId）
        if (cursorLikeCount != null && cursorId != null) {
            Criteria cursorCriteria = new Criteria().orOperator(
                    Criteria.where("likeCount").lt(cursorLikeCount),
                    Criteria.where("likeCount").is(cursorLikeCount)
                            .and("_id").lt(new ObjectId(cursorId))
            );
            query.addCriteria(cursorCriteria);
        }

        // 排序，先按likeCount降序，再按_id降序
        query.with(Sort.by(Sort.Direction.DESC, "likeCount", "_id"));

        // 限制数量（多查找 1 条用于判断是否有更多）
        query.limit(limit+1);

        // 执行查询
        List<Comment> comments = mongoTemplate.find(query, Comment.class);

        boolean hasMore = false;
        if (comments.size() > limit) {
            hasMore = true;
            // 移除多出来的那 1 条
            comments.remove(comments.size() - 1);
        }

        // 构建评论DTO
        return CommentVosDto.builder()
                .comments(convertToCommentVoList(comments))
                .total((long) comments.size())
                .hasMore(hasMore)
                .build();
    }

    /**
     * 根据评论ID和分页参数获取回复
     *
     * @param commentId 评论ID
     * @param page 页码
     * @param limit 限制
     * @return 回复分页DTO
     */
    @Override
    public CommentVosDto getReplies(String commentId, int page, int limit) {
        // 验证ObjectId格式
        if (!ObjectId.isValid(commentId)) throw new BusinessException(CommentApiStatus.INVALID_PARAM);
        // 创建分页参数，按照创建时间升序（越早越靠前）
        Pageable pageable = PageRequest.of(page, limit, Sort.by(Sort.Direction.ASC, "createdTime"));
        // 获取page对象
        Page<Comment> replyPage = commentRepository.findByParentId(commentId, Comment.STATUS_VISIBLE, pageable);
        log.info(replyPage);
        // 获取数据列表
        List<Comment> replyList = replyPage.getContent();
        log.info(replyList);
        // 构建回复DTO
        return CommentVosDto.builder()
                .comments(convertToCommentVoList(replyList))
                .total(replyPage.getTotalElements())
                .hasMore(replyPage.hasNext())
                .build();
    }
    /**
     * 点赞评论
     *
     * @param commentId 评论ID
     * @param userId 用户ID
     */
    @Override
    public void likeComment(String commentId, Long userId) {
        // 验证评论存在
        Comment comment = validateAndGetComment(commentId);
        ObjectId commentObjectId = new ObjectId(commentId);

        // 检查是否已点赞
        if (commentLikeRepository.existsByCommentIdAndUserId(commentObjectId, userId)) {
            throw new BusinessException(CommentApiStatus.DUPLICATE_COMMENT);
        }

        // 保存点赞记录
        CommentLike like = CommentLike.builder()
                .commentId(commentObjectId)
                .userId(userId)
                .createdTime(LocalDateTime.now())
                .build();
        commentLikeRepository.save(like);

        // 更新评论点赞数
        comment.setLikeCount(comment.getLikeCount() + 1);
        commentRepository.save(comment);
    }
    /**
     * 取消点赞评论
     *
     * @param commentId 评论ID
     * @param userId 用户ID
     */

    @Override
    public void unlikeComment(String commentId, Long userId) {
        // 验证评论存在
        Comment comment = validateAndGetComment(commentId);
        ObjectId commentObjectId = new ObjectId(commentId);

        // 检查是否已点赞
        if (!commentLikeRepository.existsByCommentIdAndUserId(commentObjectId, userId)) {
            throw new BusinessException(CommentApiStatus.COMMENT_NOT_FOUND);
        }

        // 删除点赞记录
        commentLikeRepository.deleteByCommentIdAndUserId(commentObjectId, userId);

        // 更新评论点赞数
        comment.setLikeCount(Math.max(0, comment.getLikeCount() - 1));
        commentRepository.save(comment);
    }

    /**
     * 删除评论
     *
     * @param commentId 评论ID
     * @param userId 用户ID
     */
    @Override
    public void deleteComment(String commentId, Long userId) {
        // 验证评论存在
        Comment comment = validateAndGetComment(commentId);
        // 检查权限（发布者）
        if (!comment.getUserId().equals(userId) ) {
            throw new BusinessException(CommentApiStatus.NOT_COMMENT_AUTHOR_OR_ADMIN);
        }

        // 软删除评论
        comment.setStatus(Comment.STATUS_DELETED);
        commentRepository.save(comment);

        // 如果是子评论，更新父评论的回复数
        if (comment.getParentId() != null && !comment.getParentId().isEmpty()) {
            // 验证父评论存在
            Comment parentComment = validateAndGetComment(comment.getParentId());
            // 更新父评论的回复数
            if (parentComment != null && parentComment.getReplyCount() > 0) {
                parentComment.setReplyCount(parentComment.getReplyCount() - 1);
                commentRepository.save(parentComment);
            }
        }
    }

    /**
     * 验证评论存在
     *
     * @param commentId 评论ID
     * @return 评论
     */
    private Comment validateAndGetComment(String commentId) {
        // 验证ObjectId格式
        if (!ObjectId.isValid(commentId)) {
            throw new BusinessException(CommentApiStatus.INVALID_PARAM);
        }
        return commentRepository.findById(new ObjectId(commentId))
                .filter(c -> c.getStatus() != Comment.STATUS_DELETED)
                .orElseThrow(() -> new BusinessException(CommentApiStatus.COMMENT_NOT_FOUND));
    }

    /**
     * 将评论列表转换为评论VO列表
     *
     * @param comments 评论列表
     * @return 评论VO列表
     */
    private List<CommentVo> convertToCommentVoList(List<Comment> comments) {
        if (comments.isEmpty()) {
            return List.of();
        }

        // 获取用户简要信息
        Set<Long> userIds = new HashSet<>();
        comments.forEach(comment -> userIds.add(comment.getUserId()));
        Map<Long, UserBriefDto> userBriefMap = userFeignClient.getUserBriefInfosByIds(userIds);

        // 获取当前用户点赞的评论ID集合
        Long currentUserId = SecurityUtils.getCurrentUserId();
        Set<ObjectId> likedCommentIds = commentLikeRepository
                .findByCommentIdInAndUserId(
                        comments.stream().map(Comment::getId).toList(),
                        currentUserId)
                .stream()
                .map(CommentLike::getCommentId)
                .collect(java.util.stream.Collectors.toSet());

        return comments.stream().map(comment -> CommentVo.builder()
                .id(String.valueOf(comment.getId()))
                .content(comment.getContent())
                .userId(comment.getUserId())
                .userName(userBriefMap.get(comment.getUserId()).getName())
                .userAvatar(userBriefMap.get(comment.getUserId()).getAvatar())
                .postId(comment.getPostId())
                .parentId(comment.getParentId())
                .createTime(comment.getCreatedTime())
                .replyCount(comment.getReplyCount())
                .likeCount(comment.getLikeCount())
                .isLike(likedCommentIds.contains(comment.getId()))
                .build()).toList();
    }
}
