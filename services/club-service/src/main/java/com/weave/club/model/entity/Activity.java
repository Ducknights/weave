package com.weave.club.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@TableName("activities")
@AllArgsConstructor
@NoArgsConstructor
public class Activity {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer clubId;
    // 社团名称（用于查询结果映射）
    @TableField(exist = false)
    private String clubName;
    private String title;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String location;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
