package org.example.model.enums;

/**
 * 帖子状态转换事件
 */
public enum PostStateEvent {
    /** 提交/创建帖子 */
    SUBMIT,

    /** 审核通过 */
    APPROVE,

    /** 审核拒绝（打回） */
    REJECT,
    /** 隐藏帖子 */
    HIDE,

    /** 恢复帖子（取消隐藏） */
    RESTORE,

    /** 删除帖子 */
    DELETE
}
