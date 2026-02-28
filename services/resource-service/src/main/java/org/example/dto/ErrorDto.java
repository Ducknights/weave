package org.example.dto;

import lombok.Builder;

@Builder
public record ErrorDto(
        String message,
        Long requestId,
        String timestamp) {
}
