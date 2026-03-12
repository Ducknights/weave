package org.example.controller;

import jakarta.annotation.Resource;
import org.example.dto.InteractionDto;
import org.example.service.InteractionService;
import org.example.util.SecurityUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

import static org.example.model.InteractionEnum.MUTE;

@RestController
@RequestMapping("/api/user/mute")
public class MuteController {

    @Resource
    private InteractionService interactionService;

    @PostMapping("/{targetUserId}")
    public void muteUser(@PathVariable Long targetUserId) {
        Long userId = SecurityUtils.getCurrentUserId();
        InteractionDto dto = new InteractionDto(userId, targetUserId, MUTE);
        interactionService.addRecord(dto);
    }

    @DeleteMapping("/{targetUserId}")
    public void unmuteUser(@PathVariable Long targetUserId) {
        Long userId = SecurityUtils.getCurrentUserId();
        InteractionDto dto = new InteractionDto(userId, targetUserId, MUTE);
        interactionService.deleteRecord(dto);
    }

    @GetMapping()
    public Set<Long> getMutedUsers(@RequestParam(defaultValue = "0") Integer page,
                                   @RequestParam(defaultValue = "20") Integer size) {
        Long userId = SecurityUtils.getCurrentUserId();
        InteractionDto dto = new InteractionDto(userId, null, MUTE);
        return interactionService.getRecord(dto, page, size);
    }
}
