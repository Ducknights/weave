package org.example.model.dto;

import lombok.Builder;

@Builder
public record PaginationDto(
        String sortBy,
        String nextCursorTime,
        String nextCursorId,
        Integer nextCursorLikeCount,
        Integer nextCursorReplyCount,
        int page,
        int limit,
        boolean hasMore
) {
}
