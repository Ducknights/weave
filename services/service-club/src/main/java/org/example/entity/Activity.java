package org.example.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("activities")
public class Activity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer id;
    private Integer clubId;
    private String title;
    private LocalDate date;
    private String time;
    private String location;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 关联的社团信息（非数据库字段）
    @TableField(exist = false)
    private Club club;

    // 社团名称（用于查询结果映射）
    @TableField(exist = false)
    private String clubName;
}
