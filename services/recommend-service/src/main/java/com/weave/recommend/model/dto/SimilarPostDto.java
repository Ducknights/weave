package com.weave.recommend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 相似帖子DTO
 *
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SimilarPostDto {
    // 帖子ID
    private Long postId;
    // 相似度分数
    private Double score;
}
