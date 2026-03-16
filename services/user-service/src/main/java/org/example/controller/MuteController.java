package org.example.controller;

import jakarta.annotation.Resource;
import org.example.dto.RelationDto;
import org.example.service.RelationService;
import org.example.util.SecurityUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

import static org.example.model.RelationEnum.MUTE;

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
    public Set<Long> getMutedUsers(@RequestParam(defaultValue = "0") Integer page,
                                   @RequestParam(defaultValue = "20") Integer size) {
        Long userId = SecurityUtils.getCurrentUserId();
        RelationDto dto = new RelationDto(userId, null, MUTE);
        return relationService.getRecord(dto, page, size);
    }
}
