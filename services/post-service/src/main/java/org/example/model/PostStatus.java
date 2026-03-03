package org.example.model;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum PostStatus {
    // 草稿
    DRAFT(1, "草稿"),
    PUBLISHED(2, "发布"),
    HIDDEN(3, "隐藏"),
    DELETED(4, "删除");

    @EnumValue
    private final int code;
    private final String desc;

    PostStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
