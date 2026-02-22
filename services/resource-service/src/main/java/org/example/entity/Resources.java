package org.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("resources")
public class Resources {
    // 资源ID
    @TableId(type = IdType.AUTO)
    private Long id;
    // 所有者ID
    private Long ownerId;
    // 标题
    private String title;
    // 内容
    private String content;
    // URL路径
    private String fileUrl;
    // 是否通过审核
    private boolean isPass;
    // 创建时间
    private LocalDateTime createdTime;
    // 更新时间
    private LocalDateTime updatedTime;
}