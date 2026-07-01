package org.example.controller;

import jakarta.annotation.Resource;
import org.example.constant.MQueue;
import org.example.model.dto.PostDetailVo;
import org.example.model.ApiResult;
import org.example.model.dto.ActionDto;
import org.example.model.eunms.UserApiStatus;
import org.example.service.ActionService;
import org.example.util.SecurityUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/api/user/{userId}")
public class UserActionController {

    @Resource
    private ActionService actionService;

    /**
     * 获取用户收藏的帖子列表
     * GET /api/user/{userId}/collect
     */
    @GetMapping("/collect")
    public ResponseEntity<ApiResult<?>> getUserCollectedPosts(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        ActionDto dto = new ActionDto(userId, null, COLLECT);
        List<PostDetailVo> vo = actionService.getRecord(dto, page, size);
        return ResponseEntity.ok().body(UserApiStatus.SUCCESS.response(vo));
    }

    /**
     * 获取用户点赞的帖子列表
     * GET /api/user/{userId}/like
     */
    @GetMapping("/like")
    public ResponseEntity<ApiResult<?>> getUserLikedPosts(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        ActionDto dto = new ActionDto(userId, null, LIKE);
        List<PostDetailVo> vo = actionService.getRecord(dto, page, size);
        return ResponseEntity.ok().body(UserApiStatus.SUCCESS.response(vo));
    }

    /**
     * 获取用户浏览历史记录
     * GET /api/user/{userId}/history
     * 仅允许查看自己的历史记录
     */
    @GetMapping("/history")
    public ResponseEntity<ApiResult<?>> getUserHistory(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId != null && !currentUserId.equals(userId)) {
            return ResponseEntity.status(403)
                    .body(UserApiStatus.PERMISSION_DENIED.response());
        }
        ActionDto dto = new ActionDto(currentUserId, null, VIEW);
        List<PostDetailVo> vo = actionService.getRecord(dto, page, size);
        return ResponseEntity.ok().body(UserApiStatus.SUCCESS.response(vo));
    }

    @GetMapping("/loadCache")
    public void loadCache(@PathVariable Long userId) {
        actionService.cacheUserAction(userId);
    }

    @RabbitListener(queues = MQueue.USER_LOGIN_QUEUE)
    public void cacheUserActions(Long userId) {
        actionService.cacheUserAction(userId);
    }
}