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
import org.example.dto.UserBriefDto;
import org.example.feign.ClubFeign;
import org.example.feign.UserFeign;
import org.example.mapper.PostMapper;
import org.example.model.entity.Post;
import org.example.model.vo.PostDetailVo;
import org.example.service.PostCommandService;
import org.example.service.PostQueryService;
import org.example.util.SecurityUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;

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
    private PostCommandService postCommandService;

    private static final long POST_CACHE_TTL = 1;

    /**
     * 根据 id 获取帖子
     */
    @Override
    public PostDetailVo getPostById(Long id, Long userId) {
        // 从缓存获取帖子
        String cacheKey = CacheKey.buildCacheKey(CacheKey.POST, id);
        try {
            if (Boolean.TRUE.equals(redisTemplate.hasKey(cacheKey))) {
                // 增加缓存帖子的浏览次数
                postCommandService.incrementViewCount(userId, id);
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
            // 增加帖子的浏览次数
            postCommandService.incrementViewCount(userId, id);
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
    public List<PostDetailVo> getRecommendPosts() {
        // 随机生成 10 个 1 到 100 之间的整数(cosplay推荐算法)
        RandomGenerator random = RandomGeneratorFactory.getDefault().create();
        List<Long> postIds = random.ints(10, 1, 101)
                .asLongStream()
                .boxed()
                .toList();
        List<Post> posts = this.listByIds(postIds);
        // 缓存帖子
        posts.forEach(this::cachePost);
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
     * 缓存帖子
     */
    private void cachePost(Post post) {
        String cacheKey = CacheKey.buildCacheKey(CacheKey.POST, post.getId());
        Map<String, Object> postMap = objectMapper.convertValue(post, new TypeReference<>() {});
        redisTemplate.opsForHash().putAll(cacheKey, postMap);
        redisTemplate.expire(cacheKey, POST_CACHE_TTL, TimeUnit.DAYS);
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
        Set<Integer> clubIds = new HashSet<>();
        for (Post post : posts) {
            if (post.getUserId() != null) userIds.add(post.getUserId());
            if (post.getClubId() != null) clubIds.add(post.getClubId());
        }
        Map<Long, UserBriefDto> userMap = userFeign.getUserInfosByIds(userIds);
        Map<Integer, ClubBriefDto> clubMap = clubFeign.getClubInfosByIds(clubIds);

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
                    boolean isLiked = getLikeStatus(cacheLikeKey, post.getId());
                    boolean isCollected = getCollectStatus(cacheCollectKey, post.getId());
                    // 构建返回的帖子详情VO
                    return PostDetailVo.builder()
                            .id(post.getId())
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
                            .shareCount(post.getShareCount())
                            .commentCount(post.getCommentCount())
                            .viewCount(post.getViewCount())
                            .createdTime(post.getCreatedTime())
                            .updatedTime(post.getUpdatedTime())
                            .build();
                })
                .toList();
    }

    /**
     * 获取点赞状态
     */
    private boolean getLikeStatus(String cacheLikeKey, Long postId) {
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(cacheLikeKey, postId));
    }

    /**
     * 获取收藏状态
     */
    private boolean getCollectStatus(String cacheCollectKey, Long postId) {
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(cacheCollectKey, postId));
    }
}
