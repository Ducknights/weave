package com.weave.model.model.enums;

import lombok.Getter;

/**
 * 帖子状态枚举
 */
@Getter
public enum PostStatus {
    DRAFT(1, "草稿"),
    PENDING(2, "审核中"),
    PUBLISHED(3, "已发布"),
    HIDDEN(4, "隐藏"),
    DELETED(5, "删除");

    private final int code;
    private final String desc;

    PostStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}