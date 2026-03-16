package org.example.controller;

import jakarta.annotation.Resource;
import org.example.dto.ActionDto;
import org.example.service.ActionService;
import org.example.service.RelationService;
import org.example.util.SecurityUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

import static org.example.model.ActionEnum.SHARE;

@RestController
@RequestMapping("/api/user/share")
public class ShareController {

    @Resource
    private ActionService actionService;

    /**
     * 分享帖子
     */
    @PostMapping("/{targetPostId}")
    public void sharePost(@PathVariable Long targetPostId) {
        Long userId = SecurityUtils.getCurrentUserId();
        ActionDto dto = new ActionDto(userId, targetPostId, SHARE);
        actionService.addRecord(dto);
    }

    /**
     * 取消分享帖子
     */
    @DeleteMapping("/{targetPostId}")
    public void unsharedPost(@PathVariable Long targetPostId) {
        Long userId = SecurityUtils.getCurrentUserId();
        ActionDto dto = new ActionDto(userId, targetPostId, SHARE);
        actionService.deleteRecord(dto);
    }

    /**
     * 获取用户分享的帖子
     */
    @GetMapping()
    public Set<Long> getUserSharedPosts(@RequestParam(defaultValue = "0") Integer page,
                                        @RequestParam(defaultValue = "20") Integer size) {
        Long userId = SecurityUtils.getCurrentUserId();
        ActionDto dto = new ActionDto(userId, null, SHARE);
        return actionService.getRecord(dto, page, size);
    }
}
