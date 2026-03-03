package org.example.controller;

import jakarta.annotation.Resource;
import org.example.bean.RequestContext;
import org.example.dto.InteractionDto;
import org.example.service.InteractionService;
import org.example.constant.CacheKey;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/share")

public class ShareController {

    @Resource
    private InteractionService interactionService;
    @Resource
    private RequestContext requestContext;

    /**
     * 分享帖子的接口
     *
     * @param targetPostId 帖子ID，通过路径变量传递
     */
    @PostMapping("/{targetPostId}")
    @CacheEvict(value = CacheKey.USER_SHARED_POSTS, key = "#requestContext.userId")
    public void sharePost(@PathVariable Long targetPostId) {
        Long userId = requestContext.getUserId();
        InteractionDto dto = new InteractionDto(userId, targetPostId, 2);
        interactionService.addRecord(dto);
    }

    /**
     * 取消分享帖子的接口
     *
     * @param targetPostId 帖子ID，通过路径变量传递
     */
    @DeleteMapping("/{targetPostId}")
    @CacheEvict(value = CacheKey.USER_SHARED_POSTS, key = "#requestContext.userId")
    public void unsharedPost(@PathVariable(value = "targetPostId") Long targetPostId) {
        Long userId = requestContext.getUserId();
        InteractionDto dto = new InteractionDto(userId, targetPostId, 2);
        interactionService.deleteRecord(dto);
    }

    /**
     * 获取用户分享的帖子列表
     * 该接口用于处理获取当前登录用户所有分享帖子的请求
     *
     * @return 返回用户分享帖子的ID列表
     */
    @GetMapping()
    @Cacheable(value = CacheKey.USER_SHARED_POSTS, key = "#requestContext.userId")
    public List<Long> getUserSharedPosts() {
        Long userId = requestContext.getUserId();
        InteractionDto dto = new InteractionDto(userId, null, 2);
        return interactionService.getRecord(dto);
    }
}