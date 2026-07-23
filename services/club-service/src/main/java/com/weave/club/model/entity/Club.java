package com.weave.club.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@TableName("clubs")
@AllArgsConstructor
@NoArgsConstructor
public class Club {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}