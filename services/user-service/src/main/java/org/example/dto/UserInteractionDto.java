package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInteractionDto {
    // 用户ID
    private Long userId;
    // 目标ID(帖子、用户)
    private Long targetId;
    // 1-点赞 2-收藏 3-转发
    // 1-关注 2-屏蔽 3-拉黑
    private Integer type;
}