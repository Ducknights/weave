package org.example.controller;

import jakarta.annotation.Resource;
import org.example.bean.RequestContext;
import org.example.dto.UserInteractionDto;
import org.example.service.InteractionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/mute")
public class MuteController {
    @Resource
    private RequestContext requestContext;
    @Resource
    private InteractionService interactionService;

    @GetMapping()
    public List<Long> getMutedUsers() {
        Long userId = requestContext.getUserId();
        UserInteractionDto dto = new UserInteractionDto(userId, null, 2);
        return interactionService.getRecord(dto);
    }

    @PostMapping("/{targetUserId}")
    public void muteUser(@PathVariable Long targetUserId) {
        Long userId = requestContext.getUserId();
        UserInteractionDto dto = new UserInteractionDto(userId, targetUserId, 2);
        interactionService.addRecord(dto);
    }

    @DeleteMapping("/{targetUserId}")
    public void unmuteUser(@PathVariable Long targetUserId) {
        Long userId = requestContext.getUserId();
        UserInteractionDto dto = new UserInteractionDto(userId, targetUserId, 2);
        interactionService.deleteRecord(dto);
    }
}
