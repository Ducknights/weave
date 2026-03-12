package org.example.controller;

import jakarta.annotation.Resource;
import org.example.dto.InteractionDto;
import org.example.service.InteractionService;
import org.example.util.SecurityUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

import static org.example.model.InteractionEnum.FAVORITE;

@RestController
@RequestMapping("/api/user/collect")
public class FavoriteController {

    @Resource
    private InteractionService interactionService;

    @PostMapping("/{targetPostId}")
    public void collectPost(@PathVariable Long targetPostId) {
        Long userId = SecurityUtils.getCurrentUserId();
        InteractionDto dto = new InteractionDto(userId, targetPostId, FAVORITE);
        interactionService.addRecord(dto);
    }

    @DeleteMapping("/{targetPostId}")
    public void uncollectedPost(@PathVariable Long targetPostId) {
        Long userId = SecurityUtils.getCurrentUserId();
        InteractionDto dto = new InteractionDto(userId, targetPostId, FAVORITE);
        interactionService.deleteRecord(dto);
    }

    @GetMapping()
    public Set<Long> getUserCollectedPosts(@RequestParam(defaultValue = "0") Integer page,
                                           @RequestParam(defaultValue = "20") Integer size) {
        Long userId = SecurityUtils.getCurrentUserId();
        InteractionDto dto = new InteractionDto(userId, null, FAVORITE);
        return interactionService.getRecord(dto, page, size);
    }
}
