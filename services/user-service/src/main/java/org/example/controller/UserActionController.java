package org.example.controller;

import jakarta.annotation.Resource;
import org.example.constant.MQueue;
import org.example.model.dto.ActionDto;
import org.example.service.ActionService;
import org.example.util.SecurityUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.example.model.eunms.ActionEnum.COLLECT;
import static org.example.model.eunms.ActionEnum.LIKE;
import static org.example.model.eunms.ActionEnum.VIEW;

/**
 * 用户操作控制器
 * 统一处理用户的收藏、点赞、浏览历史等操作
 */
@RestController
@RequestMapping("/api/user/actions")
public class UserActionController {

    @Resource
    private ActionService actionService;

    /**
     * 获取用户收藏的帖子列表
     * GET /api/user/actions/collect
     */
    @GetMapping("/collect")
    public List<Long> getUserCollectedPosts(@RequestParam(defaultValue = "0") Integer page,
                                            @RequestParam(defaultValue = "20") Integer size) {
        Long userId = SecurityUtils.getCurrentUserId();
        ActionDto dto = new ActionDto(userId, null, COLLECT);
        return actionService.getRecord(dto, page, size);
    }

    /**
     * 获取用户点赞的帖子列表
     * GET /api/user/actions/like
     */
    @GetMapping("/like")
    public List<Long> getUserLikedPosts(@RequestParam(defaultValue = "0") Integer page,
                                        @RequestParam(defaultValue = "20") Integer size) {
        Long userId = SecurityUtils.getCurrentUserId();
        ActionDto dto = new ActionDto(userId, null, LIKE);
        return actionService.getRecord(dto, page, size);
    }

    /**
     * 获取用户浏览历史记录
     * GET /api/user/actions/history
     */
    @GetMapping("/history")
    public List<Long> getUserHistory(@RequestParam(defaultValue = "0") Integer page,
                                     @RequestParam(defaultValue = "20") Integer size) {
        Long userId = SecurityUtils.getCurrentUserId();
        ActionDto dto = new ActionDto(userId, null, VIEW);
        return actionService.getRecord(dto, page, size);
    }

    @GetMapping("/loadCache")
    public void loadCache() {
        Long userId = SecurityUtils.getCurrentUserId();
        actionService.cacheUserAction(userId);
    }

    @RabbitListener(queues = MQueue.USER_LOGIN_QUEUE)
    public void cacheUserActions(Long userId) {
        actionService.cacheUserAction(userId);
    }
}