package com.weave.draft.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.weave.draft.model.enums.DraftStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 草稿实体
 * 存储用户草稿内容及其审核流程状态
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("draft")
public class Draft {
    @TableId(type = IdType.ASSIGN_ID)
    private Long draftId;    // 草稿ID
    private Long userId;     // 作者用户ID
    private Long clubId;     // 社团ID
    private String title;    // 标题
    private String content;  // 内容
    private DraftStatus status;  // 状态
    private String reviewRemark; // 审核备注（驳回原因等）
    private Long reviewerId;     // 审核人ID
    private Long publishedPostId; // 审核通过后发布生成的帖子ID
    private LocalDateTime createdTime;  // 创建时间
    private LocalDateTime updatedTime;  // 更新时间
    @TableField(exist = false)
    private List<String> resources; // 资源列表
}
