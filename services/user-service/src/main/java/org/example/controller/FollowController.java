package org.example.controller;

import jakarta.annotation.Resource;
import org.example.dto.InteractionDto;
import org.example.service.InteractionService;
import org.example.util.SecurityUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

import static org.example.model.InteractionEnum.FOLLOW;

@RestController
@RequestMapping("/api/user/follow")
public class FollowController {

    @Resource
    private InteractionService interactionService;

    @PostMapping("/{targetUserId}")
    public void followUser(@PathVariable Long targetUserId) {
        Long userId = SecurityUtils.getCurrentUserId();
        InteractionDto dto = new InteractionDto(userId, targetUserId, FOLLOW);
        interactionService.addRecord(dto);
    }

    @DeleteMapping("/{targetUserId}")
    public void unfollowUser(@PathVariable Long targetUserId) {
        Long userId = SecurityUtils.getCurrentUserId();
        InteractionDto dto = new InteractionDto(userId, targetUserId, FOLLOW);
        interactionService.deleteRecord(dto);
    }

    @GetMapping()
    public Set<Long> getUserFollowers(@RequestParam(defaultValue = "0") Integer page,
                                      @RequestParam(defaultValue = "20") Integer size) {
        Long userId = SecurityUtils.getCurrentUserId();
        InteractionDto dto = new InteractionDto(userId, null, FOLLOW);
        return interactionService.getRecord(dto, page, size);
    }
}
