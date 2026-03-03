package org.example.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import org.example.model.PostStatus;
import org.example.service.PostService;

import java.time.LocalDateTime;

@Data
@TableName("post")
public class Post {
    @TableId(type = IdType.AUTO)
    private Long id;
    // 用户ID
    private Long userId;
    // 标题
    private String title;
    // 内容
    private String content;
    // 状态
    private PostStatus status;
    // 浏览次数
    private Integer viewCount;
    // 点赞次数
    private Integer likeCount;
    // 创建时间
    private LocalDateTime createdTime;
    // 更新时间
    private LocalDateTime updatedTime;
}
