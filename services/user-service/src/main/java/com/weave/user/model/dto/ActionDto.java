package com.weave.user.model.dto;

import lombok.Builder;
import com.weave.user.model.eunms.ActionEnum;

@Builder
public record ActionDto(
        Long userId,
        Long targetId,
        ActionEnum type) {
}
