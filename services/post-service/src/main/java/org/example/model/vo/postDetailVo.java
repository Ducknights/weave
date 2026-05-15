package org.example.model.vo;

import lombok.Data;
import org.example.model.PostStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class postDetailVo {
    private Long id;            //postID
    private Long userId;        // 用户id
    private Integer clubId;     // 社团id
    private String username;    // 用户名
    private String avatar;      // 头像
    private String title;       // 标题
    private String content;     // 内容
    private List<String> urls;  // 资源地址
    private PostStatus status;  // 状态
    private Integer viewCount;  // 浏览次数
    private Integer likeCount;  // 点赞次数
    private Integer shareCount;  // 分享次数
    private Integer commentCount;  // 评论次数
    private LocalDateTime createdTime;  // 创建时间
    private LocalDateTime updatedTime;  // 更新时间
}
