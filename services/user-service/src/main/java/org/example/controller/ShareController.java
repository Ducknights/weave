package org.example.controller;

import jakarta.annotation.Resource;
import org.example.dto.InteractionDto;
import org.example.service.InteractionService;
import org.example.util.SecurityUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

import static org.example.model.InteractionEnum.SHARE;

@RestController
@RequestMapping("/api/user/share")
public class ShareController {

    @Resource
    private InteractionService interactionService;

    @PostMapping("/{targetPostId}")
    public void sharePost(@PathVariable Long targetPostId) {
        Long userId = SecurityUtils.getCurrentUserId();
        InteractionDto dto = new InteractionDto(userId, targetPostId, SHARE);
        interactionService.addRecord(dto);
    }

    @DeleteMapping("/{targetPostId}")
    public void unsharedPost(@PathVariable Long targetPostId) {
        Long userId = SecurityUtils.getCurrentUserId();
        InteractionDto dto = new InteractionDto(userId, targetPostId, SHARE);
        interactionService.deleteRecord(dto);
    }

    @GetMapping()
    public Set<Long> getUserSharedPosts(@RequestParam(defaultValue = "0") Integer page,
                                        @RequestParam(defaultValue = "20") Integer size) {
        Long userId = SecurityUtils.getCurrentUserId();
        InteractionDto dto = new InteractionDto(userId, null, SHARE);
        return interactionService.getRecord(dto, page, size);
    }
}
