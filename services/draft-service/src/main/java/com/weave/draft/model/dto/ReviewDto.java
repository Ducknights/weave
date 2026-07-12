package com.weave.draft.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 审核操作请求 DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewDto {
    /** 审核备注（驳回原因等，可选） */
    private String remark;
}
