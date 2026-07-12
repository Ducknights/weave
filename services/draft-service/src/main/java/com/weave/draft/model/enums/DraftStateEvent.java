package com.weave.draft.model.enums;

/**
 * 草稿状态转换事件
 */
public enum DraftStateEvent {
    /** 提交审核（草稿 -> 审核中） */
    SUBMIT,

    /** 审核通过（审核中 -> 审核通过） */
    APPROVE,

    /** 审核驳回（审核中 -> 审核驳回） */
    REJECT
}
