package org.example.model.dto;

import lombok.Builder;
import org.example.model.eunms.ActionEnum;

@Builder
public record ActionDto(
        Long userId,
        Long targetId,
        ActionEnum type) {
}
