package org.example.dto;

import org.example.model.RelationEnum;

public record RelationDto(
        Long userId,
        Long targetId,
        RelationEnum type) {
}
