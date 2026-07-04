package com.weave.post.model.enums;

/**
 * 帖子状态转换事件
 */
public enum PostStateEvent {
    /** 隐藏帖子 */
    HIDE,

    /** 恢复帖子（取消隐藏） */
    RESTORE,

    /** 删除帖子 */
    DELETE
}
