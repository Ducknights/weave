package com.weave.draft.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 草稿状态枚举
 */
@Getter
public enum DraftStatus {
    DRAFT(1, "草稿"),
    PENDING(2, "审核中"),
    APPROVED(3, "审核通过"),
    REJECTED(4, "审核驳回");

    @EnumValue
    private final int code;
    @JsonValue
    private final String desc;

    DraftStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
