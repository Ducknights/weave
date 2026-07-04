package com.weave.post.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.weave.post.exception.AuthorizationException;
import com.weave.post.exception.ResourceNotFoundException;
import com.weave.post.mapper.PostMapper;
import com.weave.post.mapper.PostResourceMapper;
import com.weave.post.service.PostStateMachineService;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import com.weave.redis.constant.CacheKey;
import com.weave.model.constant.PostOperation;
import com.weave.model.model.dto.SearchDocumentDto;
import com.weave.model.model.PostActionMessage;
import com.weave.model.model.PostSyncMessage;
import com.weave.post.model.dto.PostDto;
import com.weave.post.model.entity.Post;
import com.weave.post.model.entity.PostResource;
import com.weave.post.model.enums.PostActionType;
import com.weave.post.model.enums.PostStateEvent;
import com.weave.model.model.enums.PostStatus;
import com.weave.post.service.PostCommandService;
import com.weave.rabbitmq.util.MQUtil;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Service
@Transactional
public class PostCommandServiceImpl extends ServiceImpl<PostMapper, Post> implements PostCommandService {

    @Resource
    private PostMapper postMapper;
    @Resource
    private PostResourceMapper postResourceMapper;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private MQUtil mqUtil;
    @Resource
    private PostStateMachineService stateMachineService;

    /**
     * 创建帖子（初始状态: PUBLISHED 已发布）
     */
    @Override
    public void createPost(Long userId, PostDto postDto) {
        Post post = Post.builder()
                .userId(userId)
                .clubId(postDto.getClubId())
                .title(postDto.getTitle())
                .content(postDto.getContent())
                .status(PostStatus.PUBLISHED)
                .viewCount(0)
                .likeCount(0)
                .collectCount(0)
                .commentCount(0)
                .build();

        postMapper.insert(post);
        Long postId = post.getPostId();
        // 插入帖子图片
        if (postDto.getCoverImage() != null) {
            for (String coverImage : postDto.getCoverImage()) {
                PostResource postResource = PostResource.builder()
                        .postId(postId)
                        .resourcePath(coverImage)
                        .build();
                postResourceMapper.insert(postResource);
            }
        }
        // 发送同步消息
        sendPostSyncMessage(PostOperation.CREATE, post);
    }

    /**
     * 更新帖子
     */
    @Override
    @CacheEvict(value = CacheKey.POST_HASH, key = "#id")
    public void updatePost(Long id, Long userId, PostDto postDto) {
        Post post = postMapper.selectById(id);
        if (post == null) {
            throw new ResourceNotFoundException("内容不存在");
        }
        if (!post.getUserId().equals(userId)) {
            throw new AuthorizationException("没有权限修改");
        }
        post.setTitle(postDto.getTitle());
        post.setContent(postDto.getContent());
        postMapper.updateById(post);

        // 发送同步消息
        sendPostSyncMessage(PostOperation.UPDATE, post);
    }

    /**
     * 删除帖子（状态机驱动: 任意状态 -> DELETED，逻辑删除）
     */
    @Override
    @CacheEvict(value = CacheKey.POST_HASH, key = "#id")
    public void deletePost(Long id, Long userId) {
        Post post = postMapper.selectById(id);
        if (post == null) {
            throw new ResourceNotFoundException("内容不存在");
        }
        if (!post.getUserId().equals(userId)) {
            throw new AuthorizationException("没有权限删除");
        }
        // 状态机驱动: 转为 DELETED 状态
        PostStatus newStatus = stateMachineService.sendEvent(post, PostStateEvent.DELETE);
        post.setStatus(newStatus);
        postMapper.updateById(post);

        // 发送同步消息
        sendPostSyncMessage(PostOperation.DELETE, post);
    }

    /**
     * 隐藏帖子: PUBLISHED -> HIDDEN
     */
    @Override
    @CacheEvict(value = CacheKey.POST_HASH, key = "#id")
    public void hidePost(Long id, Long userId) {
        Post post = postMapper.selectById(id);
        if (post == null) {
            throw new ResourceNotFoundException("内容不存在");
        }
        if (!post.getUserId().equals(userId)) {
            throw new AuthorizationException("没有权限操作");
        }
        PostStatus newStatus = stateMachineService.sendEvent(post, PostStateEvent.HIDE);
        post.setStatus(newStatus);
        postMapper.updateById(post);
    }

    /**
     * 恢复帖子: HIDDEN -> PUBLISHED
     */
    @Override
    @CacheEvict(value = CacheKey.POST_HASH, key = "#id")
    public void restorePost(Long id, Long userId) {
        Post post = postMapper.selectById(id);
        if (post == null) {
            throw new ResourceNotFoundException("内容不存在");
        }
        if (!post.getUserId().equals(userId)) {
            throw new AuthorizationException("没有权限操作");
        }
        PostStatus newStatus = stateMachineService.sendEvent(post, PostStateEvent.RESTORE);
        post.setStatus(newStatus);
        postMapper.updateById(post);
    }

    /**
     * 添加到历史记录
     */
    @Override
    public void addToHistory(Long userId, Long postId) {
        // 参数检查
        if (userId == null || postId == null) return;
        // 增加帖子的浏览次数
        handlePostAction(userId, postId, PostActionType.VIEW);
        // 添加用户最近浏览（最近浏览功能用）- 使用ZSet按时间排序
        String userCacheKey = CacheKey.buildCacheKey(CacheKey.USER_VIEWED_POSTS, userId);
        redisTemplate.opsForZSet().add(userCacheKey, postId, System.currentTimeMillis());
    }

    /**
     * 点赞帖子
     */
    @Override
    public void like(Long userId, Long postId) {
        handlePostAction(userId, postId, PostActionType.LIKE);
    }

    /**
     * 取消点赞帖子
     */
    @Override
    public void unLike(Long userId, Long postId) {
        handlePostAction(userId, postId, PostActionType.UNLIKE);
    }

    /**
     * 收藏帖子
     */
    @Override
    public void collect(Long userId, Long postId) {
        handlePostAction(userId, postId, PostActionType.COLLECT);
    }

    /**
     * 取消收藏帖子
     */
    @Override
    public void unCollect(Long userId, Long postId) {
        handlePostAction(userId, postId, PostActionType.UNCOLLECT);
    }

    /**
     * 通用帖子操作处理方法(增加、减少统计数)
     * @param userId 用户ID
     * @param postId 帖子ID
     * @param actionType 操作类型
     */
    private void handlePostAction(Long userId, Long postId, PostActionType actionType) {
        // 更新帖子缓存
        String postCacheKey = CacheKey.buildCacheKey(CacheKey.POST_HASH, postId);
        redisTemplate.opsForHash().increment(postCacheKey, actionType.getCacheField(), actionType.isIncrement() ? 1 : -1);
        
        // 更新用户缓存
        String userCacheKey = CacheKey.buildCacheKey(actionType.getUserCacheKeyPrefix(), userId);
        if (actionType.isIncrement()) {
            redisTemplate.opsForSet().add(userCacheKey, postId);
        } else {
            redisTemplate.opsForSet().remove(userCacheKey, postId);
        }

        // 发送消息到 MQ（异步处理）
        log.info("发送帖子行为消息: userId={}, postId={}, operation={}", userId, postId, actionType.getOperation());
        sendPostActionMessage(userId, postId, actionType.getOperation());
    }

    /**
     * 更新帖子统计信息
     */
    @Override
    public void updateStats(Long postId, String action) {
        switch (action) {
            case PostOperation.VIEW -> postMapper.increaseViewCount(postId); // 浏览次数不可能减少
            case PostOperation.LIKE -> postMapper.updateLikeCount(postId, 1);
            case PostOperation.UNLIKE -> postMapper.updateLikeCount(postId, -1);
            case PostOperation.COLLECT -> postMapper.updateCollectCount(postId, 1);
            case PostOperation.UNCOLLECT -> postMapper.updateCollectCount(postId, -1);
            case PostOperation.COMMENT -> postMapper.updateCommentCount(postId, 1);
            case PostOperation.DELETE_COMMENT -> postMapper.updateCommentCount(postId, -1);
            default -> log.warn("未知操作: {}", action);
        }
    }

    /**
     * 发送帖子同步消息
     */
    private void sendPostSyncMessage(String operation, Post data) {
        // 构造同步数据
        // TODO: 实现字段 isPublic
        SearchDocumentDto searchDocumentDto = SearchDocumentDto.builder()
                .id(data.getPostId())
                .title(data.getTitle())
                .content(data.getContent())
                .isPublic(true)
                .build();
        // 构造消息
        PostSyncMessage message = PostSyncMessage.builder()
                .operation(operation)
                .data(searchDocumentDto)
                .build();
        mqUtil.sendToES(message);
    }

    /**
     * 发送帖子行为消息
     */
    private void sendPostActionMessage(Long userId, Long postId, String operation) {
        // 构造消息
        PostActionMessage message = PostActionMessage.builder()
                .userId(userId)
                .postId(postId)
                .action(operation)
                .build();
        // 发送消息
        mqUtil.sendPostAction(message);
    }
}
