package org.example.controller;

import jakarta.annotation.Resource;
import org.example.dto.RelationDto;
import org.example.service.RelationService;
import org.example.util.SecurityUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

import static org.example.model.RelationEnum.FOLLOW;

@RestController
@RequestMapping("/api/user/follow")
public class FollowController {

    @Resource
    private RelationService relationService;

    /**
     * 关注用户
     */
    @PostMapping("/{targetUserId}")
    public void followUser(@PathVariable Long targetUserId) {
        Long userId = SecurityUtils.getCurrentUserId();
        RelationDto dto = new RelationDto(userId, targetUserId, FOLLOW);
        relationService.addRecord(dto);
    }

    /**
     * 取消关注
     */
    @DeleteMapping("/{targetUserId}")
    public void unfollowUser(@PathVariable Long targetUserId) {
        Long userId = SecurityUtils.getCurrentUserId();
        RelationDto dto = new RelationDto(userId, targetUserId, FOLLOW);
        relationService.deleteRecord(dto);
    }

    /**
     * 分页查询用户关注列表
     */
    @GetMapping()
    public Set<Long> getUserFollowers(@RequestParam(defaultValue = "0") Integer page,
                                      @RequestParam(defaultValue = "20") Integer size) {
        Long userId = SecurityUtils.getCurrentUserId();
        RelationDto dto = new RelationDto(userId, null, FOLLOW);
        return relationService.getRecord(dto, page, size);
    }
}
