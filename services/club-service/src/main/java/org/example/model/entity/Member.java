package org.example.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.model.enums.ClubRole;
import org.example.model.enums.MemberStatus;

import java.time.LocalDateTime;

@Data
@Builder
@TableName("members")
@AllArgsConstructor
@NoArgsConstructor
public class Member {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer userId;
    // 用户名称（用于查询结果映射）
    @TableField(exist = false)
    private String userName;
    private Integer clubId;
    // 社团名称（用于查询结果映射）
    @TableField(exist = false)
    private String clubName;
    private ClubRole role;
    private MemberStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    // 关联的社团信息（非数据库字段）
    @TableField(exist = false)
    private Club club;
}
