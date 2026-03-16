package org.example.controller;

import jakarta.annotation.Resource;
import org.example.dto.ActionDto;
import org.example.service.ActionService;
import org.example.service.RelationService;
import org.example.util.SecurityUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

import static org.example.model.ActionEnum.LIKE;

@RestController
@RequestMapping("/api/user/like")
public class LikeController {

    @Resource
    private ActionService actionService;

    /**
     * 点赞帖子
     */
    @PostMapping("/{targetPostId}")
    public void likePost(@PathVariable Long targetPostId) {
        Long userId = SecurityUtils.getCurrentUserId();
        ActionDto dto = new ActionDto(userId, targetPostId, LIKE);
        actionService.addRecord(dto);
    }

    /**
     * 取消点赞
     */
    @DeleteMapping("/{targetPostId}")
    public void unlikePost(@PathVariable Long targetPostId) {
        Long userId = SecurityUtils.getCurrentUserId();
        ActionDto dto = new ActionDto(userId, targetPostId, LIKE);
        actionService.deleteRecord(dto);
    }

    /**
     * 分页获取用户点赞的帖子
     */
    @GetMapping()
    public Set<Long> getUserLikedPosts(@RequestParam(defaultValue = "0") Integer page,
                                       @RequestParam(defaultValue = "20") Integer size) {
        Long userId = SecurityUtils.getCurrentUserId();
        ActionDto dto = new ActionDto(userId, null, LIKE);
        return actionService.getRecord(dto, page, size);
    }
}
