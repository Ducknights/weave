package org.example.model.dto;

import org.example.model.eunms.RelationEnum;

public record RelationDto(
        Long userId,
        Long targetId,
        RelationEnum type) {
}
