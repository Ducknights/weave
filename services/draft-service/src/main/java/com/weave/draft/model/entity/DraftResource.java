package com.weave.draft.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@TableName("draft_resource")
public class DraftResource {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long draftId;
    private String resourcePath;
}
