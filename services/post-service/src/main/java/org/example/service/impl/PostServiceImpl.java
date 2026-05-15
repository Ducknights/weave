package org.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.example.constant.CacheKey;
import org.example.constant.MQueue;
import org.example.constant.Operation;
import org.example.dto.PostDto;
import org.example.entity.Post;
import org.example.mapper.PostMapper;
import org.example.model.PostActionMessage;
import org.example.model.PostSyncMessage;
import org.example.service.PostService;
import org.example.util.MQUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Log4j2
@Service
@Transactional
public class PostServiceImpl extends ServiceImpl<PostMapper, Post> implements PostService {

    @Resource
    private PostMapper postMapper;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private MQUtil mqUtil;

    @Resource
    private ObjectMapper objectMapper;

    private static final long POST_CACHE_TTL = 1; // 1天

    @Override
    public Post createPost(Long userId, PostDto postDto) {
        Post post = Post.builder()
                .userId(userId)
                .title(postDto.getTitle())
                .content(postDto.getContent())
                .likeCount(0)
                .shareCount(0)
                .commentCount(0)
                .viewCount(0)
                .build();
        postMapper.insert(post);

        // 缓存新帖子
        cachePost(post);

        // 发送同步消息
        sendPostSyncMessage(post.getId(), Operation.CREATE, userId, post);

        return post;
    }

    @Override
    public Post getPostById(Long id) {
        String cacheKey = CacheKey.buildCacheKey(CacheKey.POST, id);
        
        // 先从 Redis 获取
        Map<Object, Object> postMap = redisTemplate.opsForHash().entries(cacheKey);
        if (postMap != null && !postMap.isEmpty()) {
            return objectMapper.convertValue(postMap, Post.class);
        }

        // 从数据库获取
        Post post = postMapper.selectById(id);
        if (post != null) {
            cachePost(post);
        }
        return post;
    }

    private void cachePost(Post post) {
        String cacheKey = CacheKey.buildCacheKey(CacheKey.POST, post.getId());
        Map<String, Object> postMap = objectMapper.convertValue(post, Map.class);
        redisTemplate.opsForHash().putAll(cacheKey, postMap);
        redisTemplate.expire(cacheKey, POST_CACHE_TTL, TimeUnit.DAYS);
    }

    @Override
    public Page<Post> getPostList(int page, int size) {
        Page<Post> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Post::getStatus, 1)
               .orderByDesc(Post::getCreatedTime);
        return postMapper.selectPage(pageParam, wrapper);
    }

    @Override
    public Post updatePost(Long id, Long userId, PostDto postDto) {
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

        // 更新缓存
        cachePost(post);

        // 发送同步消息
        sendPostSyncMessage(post.getId(), "update", userId, post);

        return post;
    }

    @Override
    public boolean deletePost(Long id, Long userId) {
        Post post = postMapper.selectById(id);
        if (post == null) {
            throw new RuntimeException("内容不存在");
        }
        if (!post.getUserId().equals(userId)) {
            throw new RuntimeException("没有权限删除");
        }
        boolean success = postMapper.deleteById(id) > 0;
        
        if (success) {
            // 删除缓存
            String cacheKey = CacheKey.buildCacheKey(CacheKey.POST, id);
            redisTemplate.delete(cacheKey);

            // 发送同步消息
            sendPostSyncMessage(id, "delete", userId, null);
        }
        
        return success;
    }

    @Override
    public void incrementViewCount(Long id) {
        Post post = postMapper.selectById(id);
        if (post != null) {
            post.setViewCount(post.getViewCount() + 1);
            postMapper.updateById(post);
            // 更新缓存
            cachePost(post);
        }
    }

    @Override
    public boolean toggleLike(Long userId, Long postId) {
        String likedKey = CacheKey.buildCacheKey(CacheKey.USER_LIKED_POSTS, userId);
        Boolean isMember = redisTemplate.opsForSet().isMember(likedKey, postId);
        
        boolean increment;
        if (Boolean.TRUE.equals(isMember)) {
            // 取消点赞
            redisTemplate.opsForSet().remove(likedKey, postId);
            increment = false;
        } else {
            // 点赞
            redisTemplate.opsForSet().add(likedKey, postId);
            increment = true;
        }

        // 发送消息到 MQ
        PostActionMessage message = PostActionMessage.builder()
                .userId(userId)
                .postId(postId)
                .action("like")
                .increment(increment)
                .build();
        mqUtil.send(MQueue.POST_ACTION_ROUTING_KEY, message);

        return increment;
    }

    @Override
    public boolean toggleFavorite(Long userId, Long postId) {
        String favoriteKey = CacheKey.buildCacheKey(CacheKey.USER_FAVORITE_POSTS, userId);
        Boolean isMember = redisTemplate.opsForSet().isMember(favoriteKey, postId);
        
        boolean increment;
        if (Boolean.TRUE.equals(isMember)) {
            // 取消收藏
            redisTemplate.opsForSet().remove(favoriteKey, postId);
            increment = false;
        } else {
            // 收藏
            redisTemplate.opsForSet().add(favoriteKey, postId);
            increment = true;
        }

        // 发送消息到 MQ
        PostActionMessage message = PostActionMessage.builder()
                .userId(userId)
                .postId(postId)
                .action("favorite")
                .increment(increment)
                .build();
        mqUtil.send(MQueue.POST_ACTION_ROUTING_KEY, message);

        return increment;
    }

    @Override
    public boolean sharePost(Long userId, Long postId) {
        String sharedKey = CacheKey.buildCacheKey(CacheKey.USER_SHARED_POSTS, userId);
        redisTemplate.opsForSet().add(sharedKey, postId);

        // 发送消息到 MQ
        PostActionMessage message = PostActionMessage.builder()
                .userId(userId)
                .postId(postId)
                .action("share")
                .increment(true)
                .build();
        mqUtil.send(MQueue.POST_ACTION_ROUTING_KEY, message);

        return true;
    }

    @Override
    public void updateStats(Long postId, String action, boolean increment) {
        Post post = postMapper.selectById(postId);
        if (post == null) {
            return;
        }

        int delta = increment ? 1 : -1;
        
        switch (action) {
            case "like" -> {
                int newCount = Math.max(0, post.getLikeCount() + delta);
                post.setLikeCount(newCount);
            }
            case "favorite" -> {
                // 假设 Post 实体有 favoriteCount 字段，先用 commentCount 代替
                // post.setFavoriteCount(Math.max(0, post.getFavoriteCount() + delta));
            }
            case "share" -> {
                int newCount = Math.max(0, post.getShareCount() + delta);
                post.setShareCount(newCount);
            }
        }

        postMapper.updateById(post);
        cachePost(post);
    }

    private void sendPostSyncMessage(Long postId, String operation, Long userId, Object data) {
        PostSyncMessage message = PostSyncMessage.builder()
                .postId(postId)
                .operation(operation)
                .userId(userId)
                .data(data)
                .build();
        mqUtil.sendSyncToES(message);
    }
}
