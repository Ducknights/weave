package org.example.entity;

import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("resource")
public class Resources {
    // 资源ID
    @TableId(type = IdType.AUTO)
    private Long id;
    // 用户ID
    private Long userId;
    // 资源名称
    private String name;
    // 资源路径
    private String path;
    // 资源上传时间
    private LocalDateTime createdTime;

    public Resources(Long userId, String name ,String path) {
        this.name = name;
        this.userId = userId;
        this.path = path;
        this.createdTime = LocalDateTime.now();
    }
}