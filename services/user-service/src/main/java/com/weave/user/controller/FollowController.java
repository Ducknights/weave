package com.weave.user.controller;

import jakarta.annotation.Resource;
import com.weave.user.model.dto.RelationDto;
import com.weave.user.model.eunms.UserApiStatus;
import com.weave.user.service.RelationService;
import com.weave.security.util.SecurityUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.weave.user.model.eunms.RelationEnum.FOLLOW;

@RestController
@RequestMapping("/api/user/follow")
public class FollowController {

    @Resource
    private RelationService relationService;

    /**
     * 关注用户
     */
    @PostMapping("/{targetUserId}")
    public ResponseEntity<?> followUser(@PathVariable Long targetUserId) {
        Long userId = SecurityUtils.getCurrentUserId();
        RelationDto dto = new RelationDto(userId, targetUserId, FOLLOW);
        relationService.addRecord(dto);
        return ResponseEntity.ok().body(UserApiStatus.FOLLOW_SUCCESS.response());
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
    public ResponseEntity<?> getUserFollowers(
            @RequestParam() Long userId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        RelationDto dto = new RelationDto(userId, null, FOLLOW);
        List<Long> result = relationService.getRecord(dto, page, size);
        return ResponseEntity.ok().body(UserApiStatus.SUCCESS.response(result));
    }
}
