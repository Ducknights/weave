package org.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.example.constant.CacheKey;
import org.example.dto.ClubBriefDto;
import org.example.dto.PostDetailVo;
import org.example.dto.UserBriefDto;
import org.example.feign.ClubFeign;
import org.example.feign.RecommendFeign;
import org.example.feign.UserFeign;
import org.example.mapper.PostMapper;
import org.example.model.entity.Post;
import org.example.service.PostCommandService;
import org.example.service.PostQueryService;
import org.example.util.SecurityUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Log4j2
public class PostQueryServiceImpl extends ServiceImpl<PostMapper, Post> implements PostQueryService {

    @Resource
    private PostMapper postMapper;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private ObjectMapper objectMapper;
    @Resource
    private UserFeign userFeign;
    @Resource
    private ClubFeign clubFeign;
    @Resource
    private RecommendFeign recommendFeign;
    @Resource
    private PostCommandService postCommandService;

    private static final long POST_CACHE_TTL_DAYS = 1L;

    /**
     * 用户点击帖子详情
     */
    @Override
    public PostDetailVo getPostById(Long id, Long userId) {
        // 从缓存获取帖子
        String cacheKey = CacheKey.buildCacheKey(CacheKey.POST, id);
        try {
            if (Boolean.TRUE.equals(redisTemplate.hasKey(cacheKey))) {
                // 增加用户浏览记录
                postCommandService.addToHistory(userId, id);
                // 从缓存获取帖子
                Map<Object, Object> postMap = redisTemplate.opsForHash().entries(cacheKey);
                return objectMapper.convertValue(postMap, new TypeReference<>() {});
            }
        } catch (Exception e) {
            log.error("Redis挂了？Post ID: {}", id, e);
        }
        // 从数据库获取帖子
        Post post = postMapper.selectById(id);
        if (post == null) {
            throw new RuntimeException("内容不存在");
        }
        try {
            // 缓存帖子
            cachePost(post);
            // 增加帖子的浏览次数( 缓存和数据库同步 )
            postCommandService.addToHistory(userId, id);
            // 增加返回提的帖子浏览数
            post.setViewCount(post.getViewCount() + 1);
        } catch (Exception e) {
            log.error("Redis真挂了，快修，Post ID: {}", id, e);
        }
        // 转换为 PostDetailVo
        return convertToPostDetailVoList(List.of(post)).get(0);
    }

    /**
     * 获取推荐帖子
     */
    @Override
    public List<PostDetailVo> getRecommendPosts(Long userId) {
        // 调用推荐服务获取推荐帖子ID列表
        List<Long> recommendPostIds = recommendFeign.getRecommendations(10);
        if (recommendPostIds == null || recommendPostIds.isEmpty()){
            throw new RuntimeException("没有找到推荐的帖子");
        }
        // 根据ID列表批量获取帖子
        return getPostsByIds(recommendPostIds);
    }

    /**
     * 根据ID列表批量获取帖子
     */
    @Override
    public List<PostDetailVo> getPostsByIds(List<Long> ids) {
        // 从缓存或数据库获取帖子
        List<Post> posts = getPostsFromCacheOrDb(ids);
        // 转换为 PostDetailVo
        return convertToPostDetailVoList(posts);
    }

    /**
     * 获取最新帖子
     */
    @Override
    public Page<PostDetailVo> getNewPosts(int page, int size) {
        // 1，查询帖子
        Page<Post> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Post::getStatus, 1)
                .orderByDesc(Post::getCreatedTime);
        Page<Post> postPage = postMapper.selectPage(pageParam, wrapper);

        if (postPage.getRecords().isEmpty()) {
            Page<PostDetailVo> emptyPage = new Page<>(page, size);
            emptyPage.setTotal(0);
            return emptyPage;
        }

        // 2，转换为 PostDetailVo
        List<PostDetailVo> list = convertToPostDetailVoList(postPage.getRecords());

        // 3，组装新的分页对象
        Page<PostDetailVo> postDetailVoPage = new Page<>(page, size);
        postDetailVoPage.setRecords(list);
        postDetailVoPage.setTotal(postPage.getTotal());
        return postDetailVoPage;
    }

    /**
     * 从缓存或数据库获取帖子列表（优先缓存，缓存未命中则查询数据库）
     *
     * @param ids 帖子ID列表
     * @return 帖子ID到帖子的映射
     */
    private List<Post> getPostsFromCacheOrDb(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }

        // 先从缓存批量获取帖子
        Map<Long, Post> cachedPosts = getPostsFromCache(ids);
        Set<Long> cachedIds = cachedPosts.keySet();

        // 需要从数据库查询的帖子ID
        List<Long> needQueryIds = ids.stream()
                .filter(id -> !cachedIds.contains(id))
                .collect(Collectors.toList());

        // 记录未找到的帖子ID
        Set<Long> notFoundIds = new HashSet<>(needQueryIds);
        // 从数据库查询
        List<Post> dbPosts = this.listByIds(needQueryIds);
        // 从数据库查询结果中移除已找到的帖子ID
        dbPosts.forEach(post -> notFoundIds.remove(post.getPostId()));
        // 剩余未找到的id构建空对象
        notFoundIds.forEach(id -> dbPosts.add(Post.buildEmpty(id)));
        // 对所有帖子进行缓存
        dbPosts.forEach(this::cachePost);

        return dbPosts;
    }

    /**
     * 从缓存批量获取帖子
     */
    private Map<Long, Post> getPostsFromCache(List<Long> postIds) {
        Map<Long, Post> result = new HashMap<>();
        for (Long postId : postIds) {
            String cacheKey = CacheKey.buildCacheKey(CacheKey.POST, postId);
            Map<Object, Object> hash = redisTemplate.opsForHash().entries(cacheKey);
            if (!hash.isEmpty()) {
                Post post = objectMapper.convertValue(hash, Post.class);
                result.put(postId, post);
            }
        }
        return result;
    }

    /**
     * 获取热门帖子
     */
    @Override
    public Page<PostDetailVo> getHotPosts(int page, int size) {
        Page<Post> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Post::getStatus, 1)
                .orderByDesc(Post::getLikeCount);
        Page<Post> postPage = postMapper.selectPage(pageParam, wrapper);

        if (postPage.getRecords().isEmpty()) {
            Page<PostDetailVo> emptyPage = new Page<>(page, size);
            emptyPage.setTotal(0);
            return emptyPage;
        }

        List<PostDetailVo> list = convertToPostDetailVoList(postPage.getRecords());
        Page<PostDetailVo> postDetailVoPage = new Page<>(page, size);
        postDetailVoPage.setRecords(list);
        postDetailVoPage.setTotal(postPage.getTotal());
        return postDetailVoPage;
    }

    /**
     * 缓存帖子 (hash结构)
     */
    private void cachePost(Post post) {
        String cacheKey = CacheKey.buildCacheKey(CacheKey.POST, post.getPostId());
        Map<String, Object> postMap = objectMapper.convertValue(post, new TypeReference<>() {});
        redisTemplate.opsForHash().putAll(cacheKey, postMap);
        redisTemplate.expire(cacheKey, POST_CACHE_TTL_DAYS, TimeUnit.DAYS); // 缓存有效期为1天
    }

    /**
     * 将 Post 列表转换为 PostDetailVo 列表
     */
    private List<PostDetailVo> convertToPostDetailVoList(List<Post> posts) {
        if (posts == null || posts.isEmpty()) {
            return List.of();
        }

        // 1，查询用户和社团信息
        Set<Long> userIds = new HashSet<>();
        Set<Long> clubIds = new HashSet<>();
        for (Post post : posts) {
            if (post.getUserId() != null) userIds.add(post.getUserId());
            if (post.getClubId() != null) clubIds.add(post.getClubId());
        }
        Map<Long, UserBriefDto> userMap = userFeign.getUserInfosByIds(userIds);
        Map<Long, ClubBriefDto> clubMap = clubFeign.getClubInfosByIds(clubIds);

        // 获取当前用户 ID
        Long currentUserId = SecurityUtils.getCurrentUserId();
        // 1.1，查询用户点赞和收藏的缓存信息
        String cacheLikeKey = CacheKey.buildCacheKey(CacheKey.USER_LIKED_POSTS, currentUserId);
        String cacheCollectKey = CacheKey.buildCacheKey(CacheKey.USER_COLLECTED_POSTS, currentUserId);
        // 2，转换为 PostDetailVo
        return posts.stream()
                .map(post -> {
                    UserBriefDto user = userMap.get(post.getUserId());
                    ClubBriefDto club = clubMap.get(post.getClubId());
                    // 查询用户点赞和收藏的缓存信息
                    boolean isLiked = getStatus(cacheLikeKey, post.getPostId());
                    boolean isCollected = getStatus(cacheCollectKey, post.getPostId());
                    // 构建返回的帖子详情VO
                    return PostDetailVo.builder()
                            .id(post.getPostId())
                            .userId(post.getUserId())
                            .username(user != null ? user.getName() : null)
                            .clubId(post.getClubId())
                            .clubName(club != null ? club.getName() : null)
                            .avatar(user != null ? user.getAvatar() : null)
                            .title(post.getTitle())
                            .content(post.getContent())
                            .likeCount(post.getLikeCount())
                            .likeStatus(isLiked)
                            .collectCount(post.getCollectCount())
                            .collectStatus(isCollected)
                            .commentCount(post.getCommentCount())
                            .viewCount(post.getViewCount())
                            .createdTime(post.getCreatedTime())
                            .updatedTime(post.getUpdatedTime())
                            .build();
                })
                .toList();
    }

    /**
     * 获取状态(点赞、收藏)
     */
    private boolean getStatus(String cacheKey, Long postId) {
        // 1. 先尝试直接判断成员（命中则直接返回）
        Boolean isMember = redisTemplate.opsForSet().isMember(cacheKey, postId);
        if (Boolean.TRUE.equals(isMember)) {
            return true;
        }
        // 2. 检查 key 是否存在，存在说明确实不是成员
        if (Boolean.TRUE.equals(redisTemplate.hasKey(cacheKey))) {
            return false;
        }
        // 3. 加载缓存
        // TODO：使用分布式锁
        loadCache();
        // 4. 再次判断成员
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(cacheKey, postId));
    }

    /**
     * 加载缓存（用户点赞和收藏的帖子）
     */
    public void loadCache() {
        // TODO: 加载缓存（用户点赞和收藏的帖子）
    }
}
