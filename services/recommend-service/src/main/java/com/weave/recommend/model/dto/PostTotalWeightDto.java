package com.weave.recommend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostTotalWeightDto {
    private Long postId;
    private Double totalWeight;
}
