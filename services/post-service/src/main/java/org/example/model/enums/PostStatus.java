package org.example.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 帖子状态枚举
 */
@Getter
public enum PostStatus {
    PENDING(1, "待审核"),
    PUBLISHED(2, "已发布"),
    HIDDEN(3, "隐藏"),
    DELETED(4, "删除");

    @EnumValue
    private final int code;
    @JsonValue
    private final String desc;

    PostStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
