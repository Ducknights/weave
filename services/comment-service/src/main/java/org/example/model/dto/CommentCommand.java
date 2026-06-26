package org.example.model.dto;

public record CommentCommand (
    Long postId,
    String parentId,
    String content
    ){
}
