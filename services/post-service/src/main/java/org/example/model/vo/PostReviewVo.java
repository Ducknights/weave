package org.example.model.vo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PostReviewVo {
    private Long postId;
    private Long clubId;
    private String title;
    private String content;
    private List<String> resources;
}
