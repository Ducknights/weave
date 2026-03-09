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
@RequestMapping("/api/user/mute")
public class MuteController {
    @Resource
    private InteractionService interactionService;

    @GetMapping()
    public List<Long> getMutedUsers() {
        Long userId = SecurityUtils.getCurrentUserId();
        InteractionDto dto = new InteractionDto(userId, null, 2);
        return interactionService.getRecord(dto);
    }

    @PostMapping("/{targetUserId}")
    @CacheEvict(value = CacheKey.USER_MUTED_USERS, key = "#userId")
    public void muteUser(@PathVariable Long targetUserId) {
        Long userId = SecurityUtils.getCurrentUserId();
        InteractionDto dto = new InteractionDto(userId, targetUserId, 2);
        interactionService.addRecord(dto);
    }

    @DeleteMapping("/{targetUserId}")
    @CacheEvict(value = CacheKey.USER_MUTED_USERS, key = "#userContext.userId")
    public void unmuteUser(@PathVariable Long targetUserId) {
        Long userId = SecurityUtils.getCurrentUserId();
        InteractionDto dto = new InteractionDto(userId, targetUserId, 2);
        interactionService.deleteRecord(dto);
    }
}
