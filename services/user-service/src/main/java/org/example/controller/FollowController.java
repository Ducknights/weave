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
@RequestMapping("/api/user/follow")
public class FollowController {
    @Resource
    private RequestContext requestContext;
    @Resource
    private InteractionService interactionService;

    /**
     * 获取用户粉丝列表的接口方法
     * 通过GET请求调用，返回当前用户粉丝的用户ID列表
     *
     * @return 用户粉丝的ID列表，类型为List<Long>
     */
    @GetMapping()
    @Cacheable(value = CacheKey.USER_FOLLOWERS, key = "#requestContext.userId")
    public List<Long> getUserFollowers() {
        Long userId = requestContext.getUserId();
        InteractionDto dto = new InteractionDto(userId, null, 1); // 1-关注 2-屏蔽 3-拉黑
        return interactionService.getRecord(dto);
    }

    /**
     * 关注用户的接口方法
     *
     * @param targetUserId 被关注用户的ID
     */
    @PostMapping("/{targetUserId}")
    @CacheEvict(value = CacheKey.USER_FOLLOWERS, key = "#requestContext.userId")
    public void followUser(@PathVariable Long targetUserId) {
        Long userId = requestContext.getUserId();
        InteractionDto dto = new InteractionDto(userId, targetUserId, 1);
        // 调用交互服务添加关注关系
        interactionService.addRecord(dto);
    }

    /**
     * 取消关注用户的接口方法
     *
     * @param targetUserId 被取消关注用户的ID
     */
    @DeleteMapping("/{targetUserId}")
    @CacheEvict(value = CacheKey.USER_FOLLOWERS, key = "#requestContext.userId")
    public void unfollowUser(@PathVariable Long targetUserId) {
        Long userId = requestContext.getUserId();
        InteractionDto dto = new InteractionDto(userId, targetUserId, 1);
        interactionService.deleteRecord(dto);
    }
}
