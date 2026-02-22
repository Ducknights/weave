package org.example.controller;

import jakarta.annotation.Resource;
import org.example.bean.RequestContext;
import org.example.dto.UserInteractionDto;
import org.example.service.InteractionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/collect")
public class CollectController {

    @Resource
    private InteractionService interactionService;
    @Resource
    private RequestContext requestContext;

    /**
     * 收藏帖子的接口
     *
     * @param targetPostId 帖子ID，通过路径变量传递
     */
    @PostMapping("/{targetPostId}")
    public void collectPost(@PathVariable Long targetPostId) {
        Long userId = requestContext.getUserId();
        UserInteractionDto dto = new UserInteractionDto(userId, targetPostId, 3);
        interactionService.addRecord(dto);
    }

    /**
     * 取消收藏帖子的接口
     *
     * @param targetPostId 帖子ID，通过路径变量传递
     */
    @DeleteMapping("/{targetPostId}")
    public void uncollectedPost(@PathVariable(value = "targetPostId") Long targetPostId) {
        Long userId = requestContext.getUserId();
        UserInteractionDto dto = new UserInteractionDto(userId, targetPostId, 3);
        interactionService.deleteRecord(dto);
    }

    /**
     * 获取用户收藏的帖子列表
     * 该接口用于处理获取当前登录用户所有收藏帖子的请求
     *
      * @return 返回用户收藏帖子的ID列表
     */
    @GetMapping()
    public List<Long> getUserCollectedPosts() {
        Long userId = requestContext.getUserId();
        UserInteractionDto dto = new UserInteractionDto(userId, null, 3);
        return interactionService.getRecord(dto);
    }
}
