package org.example.model.vo;

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
    public LocalDateTime createTime;
    public Integer replyCount;
}
