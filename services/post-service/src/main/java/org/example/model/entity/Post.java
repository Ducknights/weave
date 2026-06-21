package org.example.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.model.enums.PostStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("post")
public class Post {
    @TableId(type = IdType.ASSIGN_ID)
    private Long postId;    //postID
    private Long userId;  // 用户id
    private Long clubId;  // 社团id
    private String title;   // 标题
    private String content; // 内容
    private PostStatus status;  // 状态
    private Integer viewCount;  // 浏览次数
    private Integer likeCount;  // 点赞次数
    private Integer collectCount;  // 收藏次数
    private Integer commentCount;  // 评论次数
    private LocalDateTime createdTime;  // 创建时间
    private LocalDateTime updatedTime;  // 更新时间
    @TableField(exist = false)
    private List<String> resources; // 资源列表

    // 构建空对象
    public static Post buildEmpty (Long postId) {
        return new Post(postId, null, null, null, null, null, null, null, null, null, null, null, null);
    }
}
