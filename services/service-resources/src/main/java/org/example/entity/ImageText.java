package org.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("image_text")
public class ImageText {
    @TableId(type = IdType.AUTO)
    private Integer imageTextResourcesId;

    private Integer ownerId;
    private String imagePath;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private boolean isPass;
}