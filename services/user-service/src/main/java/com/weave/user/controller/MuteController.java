package com.weave.user.controller;

import jakarta.annotation.Resource;
import com.weave.user.model.dto.RelationDto;
import com.weave.user.service.RelationService;
import com.weave.security.util.SecurityUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.weave.user.model.eunms.RelationEnum.MUTE;

@RestController
@RequestMapping("/api/user/mute")
public class MuteController {

    @Resource
    private RelationService relationService;

    /**
     * 屏蔽用户
     */
    @PostMapping("/{targetUserId}")
    public void muteUser(@PathVariable Long targetUserId) {
        Long userId = SecurityUtils.getCurrentUserId();
        RelationDto dto = new RelationDto(userId, targetUserId, MUTE);
        relationService.addRecord(dto);
    }

    /**
     * 解除屏蔽用户
     */
    @DeleteMapping("/{targetUserId}")
    public void unmuteUser(@PathVariable Long targetUserId) {
        Long userId = SecurityUtils.getCurrentUserId();
        RelationDto dto = new RelationDto(userId, targetUserId, MUTE);
        relationService.deleteRecord(dto);
    }

    /**
     * 分页获取被屏蔽的用户
     */
    @GetMapping()
    public List<Long> getMutedUsers(@RequestParam(defaultValue = "0") Integer page,
                                    @RequestParam(defaultValue = "20") Integer size) {
        Long userId = SecurityUtils.getCurrentUserId();
        RelationDto dto = new RelationDto(userId, null, MUTE);
        return relationService.getRecord(dto, page, size);
    }
}
