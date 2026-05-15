package org.example.model.dto;

public record CommentPageDto(
        CommentDto commentDto,
        PaginationDto paginationDto) {
}
