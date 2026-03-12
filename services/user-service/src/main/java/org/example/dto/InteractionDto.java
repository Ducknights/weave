package org.example.dto;


import org.example.model.InteractionEnum;

public record InteractionDto(
        Long userId,
        Long targetId,
        // 1-点赞 2-收藏 3-转发
        // 1-关注 2-屏蔽 3-拉黑
        InteractionEnum type) {
}
