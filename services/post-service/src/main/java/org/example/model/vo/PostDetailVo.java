package org.example.model.vo;

import lombok.Builder;
import lombok.Data;
import org.example.model.enums.PostStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class PostDetailVo {
    private Long id;            //postID
    private Long userId;        // 用户id
    private String username;    // 用户名
    private Integer clubId;     // 社团id
    private String clubName;    // 社团名
    private String avatar;      // 头像
    private String title;       // 标题
    private String content;     // 内容
    private List<String> urls;  // 资源地址
    private PostStatus status;  // 状态
    private Integer viewCount;  // 浏览次数
    private Integer likeCount;  // 点赞次数
    private Boolean likeStatus;  // 点赞状态（用户是否点赞）
    private Integer collectCount;  // 收藏次数
    private Boolean collectStatus;  // 收藏状态（用户是否收藏）
    private Integer shareCount;  // 分享次数
    private Integer commentCount;  // 评论次数
    private LocalDateTime createdTime;  // 创建时间
    private LocalDateTime updatedTime;  // 更新时间
}
