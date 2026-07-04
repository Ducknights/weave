package com.weave.user.model.dto;

import com.weave.user.model.eunms.RelationEnum;

public record RelationDto(
        Long userId,
        Long targetId,
        RelationEnum type) {
}
