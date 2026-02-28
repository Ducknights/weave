package org.example.controller;


import jakarta.annotation.Resource;
import org.example.bean.RequestContext;
import org.example.dto.InteractionDto;
import org.example.service.InteractionService;
import org.example.strings.CacheKey;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/like")

public class LikeController {

    @Resource
    private InteractionService interactionService;
    @Resource
    private RequestContext requestContext;

    /**
      * 用户点赞帖子的接口
     *
     * @param targetPostId 帖子ID，通过路径变量传递
     */
    @PostMapping("/{targetPostId}")
    @CacheEvict(value = CacheKey.USER_LIKED_POSTS, key = "#requestContext.userId")
    public void likePost(@PathVariable Long targetPostId) {
        Long userId = requestContext.getUserId();
        InteractionDto dto = new InteractionDto(userId, targetPostId, 1);
        interactionService.addRecord(dto);
    }

    /**
      * 用户取消点赞帖子的接口
     *
     * @param targetPostId 帖子ID，通过路径变量传递
     */
    @DeleteMapping("/{targetPostId}")
    @CacheEvict(value = CacheKey.USER_LIKED_POSTS, key = "#requestContext.userId")
    public void unlikePost(@PathVariable Long targetPostId) {
        Long userId = requestContext.getUserId();
        InteractionDto dto = new InteractionDto(userId, targetPostId, 1);
        interactionService.deleteRecord(dto);
    }

    /**
      * 获取用户点赞的帖子列表的接口
     *
      * @return 返回用户点赞的帖子ID列表的接口
     */
    @GetMapping()
    @Cacheable(value = CacheKey.USER_LIKED_POSTS, key = "#requestContext.userId")
    public List<Long> getUserLikedPosts() {
        Long userId = requestContext.getUserId();
        InteractionDto dto = new InteractionDto(userId, null, 1);
        return interactionService.getRecord(dto);
    }
}
