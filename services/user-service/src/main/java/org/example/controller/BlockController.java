package org.example.controller;

import jakarta.annotation.Resource;
import org.example.util.SecurityUtils;
import org.example.dto.InteractionDto;
import org.example.service.InteractionService;
import org.example.constant.CacheKey;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/block")
public class BlockController {
    @Resource
    private InteractionService interactionService;

    /**
     * 获取被屏蔽用户列表的接口方法
     * 通过HTTP GET请求调用，返回被屏蔽用户的ID列表
     *
     * @return List<Long> 返回被屏蔽用户的ID列表
     */
    @GetMapping()
    @Cacheable(value = CacheKey.USER_BLOCKED_USERS, key = "#userContext.userId")
    public List<Long> getBlockedUsers() {
        Long userId = SecurityUtils.getCurrentUserId();
        InteractionDto dto = new InteractionDto(userId, null, 3);
        return interactionService.getRecord(dto);
    }

    /**
     * 处理用户拉黑请求的接口方法
     *
     * @param targetUserId 被拉黑用户的ID，通过路径变量传递
     */
    @PostMapping("/{targetUserId}")
    @CacheEvict(value = CacheKey.USER_BLOCKED_USERS, key = "#userContext.userId")
    public void blockUser(@PathVariable Long targetUserId) {
        Long userId = SecurityUtils.getCurrentUserId();
        InteractionDto dto = new InteractionDto(userId, targetUserId, 3);
        interactionService.addRecord(dto);
    }

    /**
     * 解除用户封禁的接口方法
     * 通过DELETE请求方式，根据目标用户ID解除当前用户对目标用户的封禁关系
     *
     * @param targetUserId 路径变量，表示要解除封禁的目标用户ID
     */
    @DeleteMapping("/{targetUserId}")
    @CacheEvict(value = CacheKey.USER_BLOCKED_USERS, key = "#userContext.userId")
    public void unblockUser(@PathVariable Long targetUserId) {
        Long userId = SecurityUtils.getCurrentUserId();
        InteractionDto dto = new InteractionDto(userId, targetUserId, 3);
        interactionService.deleteRecord(dto);
    }
}
