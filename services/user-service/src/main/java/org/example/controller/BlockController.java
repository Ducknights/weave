package org.example.controller;

import jakarta.annotation.Resource;
import org.example.dto.RelationDto;
import org.example.util.SecurityUtils;
import org.example.service.RelationService;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

import static org.example.model.RelationEnum.BLOCK;

@RestController
@RequestMapping("/api/user/block")
public class BlockController {
    @Resource
    private RelationService relationService;

    /**
     * 处理用户拉黑请求的接口方法
     *
     * @param targetUserId 被拉黑用户的ID，通过路径变量传递
     */
    @PostMapping("/{targetUserId}")
    public void blockUser(@PathVariable Long targetUserId) {
        Long userId = SecurityUtils.getCurrentUserId();
        RelationDto dto = new RelationDto(userId, targetUserId, BLOCK);
        relationService.addRecord(dto);
    }

    /**
     * 解除用户封禁的接口方法
     * 通过DELETE请求方式，根据目标用户ID解除当前用户对目标用户的封禁关系
     *
     * @param targetUserId 路径变量，表示要解除封禁的目标用户ID
     */
    @DeleteMapping("/{targetUserId}")
    public void unblockUser(@PathVariable Long targetUserId) {
        Long userId = SecurityUtils.getCurrentUserId();
        RelationDto dto = new RelationDto(userId, targetUserId, BLOCK);
        relationService.deleteRecord(dto);
    }

    /**
     * 分页获取被屏蔽用户列表的接口方法
     * 通过HTTP GET请求调用，返回被屏蔽用户的ID列表
     *
     * @return List<Long> 返回被屏蔽用户的ID列表
     */
    @GetMapping()
    public Set<Long> getBlockedUsers(@RequestParam(defaultValue = "0") Integer page,
                                     @RequestParam(defaultValue = "20") Integer size) {
        Long userId = SecurityUtils.getCurrentUserId();
        RelationDto dto = new RelationDto(userId, null, BLOCK);
        return relationService.getRecord(dto, page, size);
    }
}
