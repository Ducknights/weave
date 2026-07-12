package com.weave.draft.model.vo;

import com.weave.draft.model.enums.DraftStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 草稿详情 VO
 */
@Data
@Builder
public class DraftVo {
    private Long draftId;
    private Long userId;
    private Long clubId;
    private String title;
    private String content;
    private DraftStatus status;
    private String reviewRemark;
    private Long publishedPostId;
    private List<String> resources;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
}
