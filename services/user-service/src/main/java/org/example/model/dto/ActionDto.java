package org.example.model.dto;

import org.example.model.eunms.ActionEnum;

public record ActionDto(
        Long userId,
        Long targetId,
        ActionEnum type) {
}
