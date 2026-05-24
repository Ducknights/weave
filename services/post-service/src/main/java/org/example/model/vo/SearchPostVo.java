package org.example.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SearchPostVo {
    private Long id;
    private String title;
    private String content;
    private String authorName;
    private String authorAvatar;
    private Integer likeCount;
    private Integer commentCount;
    private LocalDateTime createdAt;
    private Float score;
}
