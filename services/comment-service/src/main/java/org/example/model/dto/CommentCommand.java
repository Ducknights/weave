package org.example.model.dto;

public record CommentCommand (
    String resourceId,
    String parentId,
    String content
    ){
}
