package com.weave.post.model.enums;

/**
 * 帖子状态转换事件
 * 草稿与审核相关事件已解耦至 draft-service。
 */
public enum PostStateEvent {
    /** 隐藏帖子 */
    HIDE,

    /** 恢复帖子（取消隐藏） */
    RESTORE,

    /** 删除帖子 */
    DELETE
}
