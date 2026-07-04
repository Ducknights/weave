package com.weave.recommend.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.OrderBy;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.weave.recommend.model.enums.ActionEnum;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserAction {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Long userId;
    private Long postId;
    private ActionEnum type;
    @OrderBy()
    private LocalDateTime createdTime;
}
