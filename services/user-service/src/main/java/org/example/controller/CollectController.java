package org.example.controller;

import jakarta.annotation.Resource;
import org.example.model.dto.ActionDto;
import org.example.service.ActionService;
import org.example.util.SecurityUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.example.model.eunms.ActionEnum.COLLECT;

@RestController
@RequestMapping("/api/user/collect")
public class CollectController {

    @Resource
    private ActionService actionService;

    /**
     * 收藏帖子
     */
    @PostMapping("/{targetPostId}")
    public void collectPost(@PathVariable Long targetPostId) {
        Long userId = SecurityUtils.getCurrentUserId();
        ActionDto dto = new ActionDto(userId, targetPostId, COLLECT);
        actionService.addRecord(dto);
    }

    /**
     * 取消收藏
     */
    @DeleteMapping("/{targetPostId}")
    public void uncollectedPost(@PathVariable Long targetPostId) {
        Long userId = SecurityUtils.getCurrentUserId();
        ActionDto dto = new ActionDto(userId, targetPostId, COLLECT);
        actionService.deleteRecord(dto);
    }

    /**
     * 分页获取用户收藏的帖子
     */
    @GetMapping()
    public List<Long> getUserCollectedPosts(@RequestParam(defaultValue = "0") Integer page,
                                            @RequestParam(defaultValue = "20") Integer size) {
        Long userId = SecurityUtils.getCurrentUserId();
        ActionDto dto = new ActionDto(userId, null, COLLECT);
        return actionService.getRecord(dto, page, size);
    }
}
