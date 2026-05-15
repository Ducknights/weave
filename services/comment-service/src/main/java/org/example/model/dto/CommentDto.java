package org.example.model.dto;

import lombok.Builder;
import org.example.model.entity.Comment;

import java.util.List;

/**
 * 分页查询评论结果
 */
@Builder
public record CommentDto(
        List<Comment> comments){;
}
