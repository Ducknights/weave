package org.example.entity;

import lombok.Data;

@Data
public class UserPost {
    private Long id;
    private Long userId;
    private Long postId;
    private Integer type; // 1:点赞 2:收藏 3:转发
}
