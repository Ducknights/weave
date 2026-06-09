package org.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.example.constant.CacheKey;
import org.example.constant.PostOperation;
import org.example.dto.SearchDocumentDto;
import org.example.mapper.PostMapper;
import org.example.mapper.PostResourceMapper;
import org.example.model.PostActionMessage;
import org.example.model.PostSyncMessage;
import org.example.model.dto.PostDto;
import org.example.model.entity.Post;
import org.example.model.entity.PostResource;
import org.example.model.enums.PostActionType;
import org.example.service.PostCommandService;
import org.example.util.MQUtil;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

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

    /**
     * 创建帖子
     */
    @Override
    public void createPost(Long userId, PostDto postDto) {
        // 插入帖子
        Post post = Post.builder()
                .userId(userId)
                .clubId(postDto.getClubId())
                .title(postDto.getTitle())
                .content(postDto.getContent())
                .viewCount(0)       //浏览
                .likeCount(0)       //点赞
                .collectCount(0)    //收藏
                .commentCount(0)   //评论
                .build();
        postMapper.insert(post);
        // 获取帖子ID
        Long postId = post.getPostId();
        // 插入帖子图片
        for (String coverImage : postDto.getCoverImage()) {
            PostResource postResource = PostResource.builder()
                    .postId(postId)
                    .resourcePath(coverImage)
                    .build();
            postResourceMapper.insert(postResource);
        }
        // 发送同步消息
        sendPostSyncMessage(PostOperation.CREATE, post);
    }

    /**
     * 更新帖子
     */
    @Override
    @CacheEvict(value = CacheKey.POST, key = "#id")
    public void updatePost(Long id, Long userId, PostDto postDto) {
        Post post = postMapper.selectById(id);
        if (post == null) {
            throw new RuntimeException("内容不存在");
        }
        if (!post.getUserId().equals(userId)) {
            throw new RuntimeException("没有权限修改");
        }
        post.setTitle(postDto.getTitle());
        post.setContent(postDto.getContent());
        postMapper.updateById(post);

        // 发送同步消息
        sendPostSyncMessage(PostOperation.UPDATE, post);
    }

    /**
     * 删除帖子
     */
    @Override
    @CacheEvict(value = CacheKey.POST, key = "#id")
    public void deletePost(Long id, Long userId) {
        Post post = postMapper.selectById(id);
        if (post == null) {
            throw new RuntimeException("内容不存在");
        }
        if (!post.getUserId().equals(userId)) {
            throw new RuntimeException("没有权限删除");
        }
        if (postMapper.deleteById(id) > 0) {
            // 发送同步消息
            sendPostSyncMessage(PostOperation.DELETE, post);
        }
    }

    /**
     * 添加到历史记录
     */
    @Override
    public void addToHistory(Long userId, Long postId) {
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
    public void unlike(Long userId, Long postId) {
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
    public void uncollect(Long userId, Long postId) {
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
        String postCacheKey = CacheKey.buildCacheKey(CacheKey.POST, postId);
        redisTemplate.opsForHash().increment(postCacheKey, actionType.getCacheField(), actionType.isIncrement() ? 1 : -1);
        
        // 更新用户缓存
        String userCacheKey = CacheKey.buildCacheKey(actionType.getUserCacheKeyPrefix(), userId);
        if (actionType.isIncrement()) {
            redisTemplate.opsForSet().add(userCacheKey, postId);
        } else {
            redisTemplate.opsForSet().remove(userCacheKey, postId);
        }

        // 发送消息到 MQ（异步处理）
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
                .build();
        // 构造消息
        PostSyncMessage message = PostSyncMessage.builder()
                .operation(operation)
                .data(searchDocumentDto)
                .build();
        mqUtil.sendSyncToES(message);
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
        mqUtil.sendToPostAction(message);
    }
}
