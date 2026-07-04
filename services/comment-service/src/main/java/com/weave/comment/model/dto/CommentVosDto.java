package com.weave.comment.model.dto;

import com.weave.comment.model.vo.CommentVo;
import lombok.Builder;

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
