package com.weave.comment.model.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommentVo {
    public String id;
    public String parentId;
    public Long postId;
    public Long userId;
    public String userName;
    public String userAvatar;
    public String content;
    public Integer likeCount;
    public Boolean isLike;
    public LocalDateTime createTime;
    public Integer replyCount;
}
