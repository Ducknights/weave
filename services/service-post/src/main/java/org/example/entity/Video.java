package org.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("video")
public class Video {
    @TableId(type = IdType.AUTO)
    private Integer videoResourcesId;

    private Integer ownerId;
    private String videoPath;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private boolean isPass;
}