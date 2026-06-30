package org.example.model.dto;

import lombok.Builder;
import org.example.model.enums.ActionEnum;

@Builder
public record ActionDto(
        Long userId,
        Long postId,
        ActionEnum type) {
}