package com.weave.comment.model.dto;

public record CommentCommand (
    Long postId,
    String parentId,
    String content
    ){
}
