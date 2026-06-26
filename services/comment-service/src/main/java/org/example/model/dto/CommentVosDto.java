package org.example.model.dto;

import lombok.Builder;
import org.example.model.vo.CommentVo;

import java.util.List;

/**
 * 分页查询评论结果
 */
@Builder
public record CommentVosDto(
        List<CommentVo> comments,
        Long total,
        boolean hasMore){
}
