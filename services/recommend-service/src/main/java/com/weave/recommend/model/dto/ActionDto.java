package com.weave.recommend.model.dto;

import lombok.Builder;
import com.weave.recommend.model.enums.ActionEnum;

@Builder
public record ActionDto(
        Long userId,
        Long postId,
        ActionEnum type) {
}