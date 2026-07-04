package com.weave.post.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 帖子状态枚举
 */
@Getter
public enum PostStatus {
    PUBLISHED(1, "已发布"),
    HIDDEN(2, "隐藏"),
    DELETED(3, "删除");

    @EnumValue
    private final int code;
    @JsonValue
    private final String desc;

    PostStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
