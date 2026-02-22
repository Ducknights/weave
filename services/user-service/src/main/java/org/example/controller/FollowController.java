package org.example.controller;

import jakarta.annotation.Resource;
import org.example.bean.RequestContext;
import org.example.dto.UserInteractionDto;
import org.example.service.InteractionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/follow")
public class FollowController {
    @Resource
    private RequestContext requestContext;
    @Resource
    private InteractionService interactionService;

    /**
     * 获取用户粉丝列表的接口方法
     * 通过GET请求调用，返回当前用户粉丝的用户ID列表
     *
     * @return 用户粉丝的ID列表，类型为List<Long>
     */
    @GetMapping()
    public List<Long> getUserFollowers() {
        Long userId = requestContext.getUserId();
        UserInteractionDto dto = new UserInteractionDto(userId, null, 1); // 1-关注 2-屏蔽 3-拉黑
        return interactionService.getRecord(dto);
    }

    /**
     * 关注用户的接口方法
     *
     * @param targetUserId 被关注用户的ID
     */
    @PostMapping("/{targetUserId}")
    public void followUser(@PathVariable Long targetUserId) {
        Long userId = requestContext.getUserId();
        UserInteractionDto dto = new UserInteractionDto(userId, targetUserId, 1);
    // 调用交互服务添加关注关系
        interactionService.addRecord(dto);
    }

    /**
     * 取消关注用户的接口方法
     *
     * @param targetUserId 被取消关注用户的ID
     */
    @DeleteMapping("/{targetUserId}")
    public void unfollowUser(@PathVariable Long targetUserId) {
        Long userId = requestContext.getUserId();
        UserInteractionDto dto = new UserInteractionDto(userId, targetUserId, 1);
        interactionService.deleteRecord(dto);
    }
}
