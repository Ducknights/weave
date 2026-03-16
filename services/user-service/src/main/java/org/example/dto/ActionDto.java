package org.example.dto;

import org.example.model.ActionEnum;

public record ActionDto(
        Long userId,
        Long targetId,
        ActionEnum type) {
}
