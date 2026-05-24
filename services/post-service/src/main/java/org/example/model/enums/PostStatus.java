package org.example.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
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

    @EnumValue
    private final int code;
    @JsonValue
    private final String desc;

    PostStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
