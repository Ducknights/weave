package org.example.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@TableName("members")
public class Member implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Integer id;
    private Integer userId;
    // 用户名称（用于查询结果映射）
    @TableField(exist = false)
    private String userName;
    private Integer clubId;
    // 社团名称（用于查询结果映射）
    @TableField(exist = false)
    private String clubName;
    private String role;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    // 关联的社团信息（非数据库字段）
    @TableField(exist = false)
    private Club club;
}
