package com.weave.post.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@TableName("post_resource")
public class PostResource {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long postId;
    private String resourcePath;
}
