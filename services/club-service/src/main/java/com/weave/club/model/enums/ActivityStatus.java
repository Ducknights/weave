package com.weave.club.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum ActivityStatus {
    PENDING(1, "审核中"),
    UPCOMING(2, "即将开始"),
    ONGOING(3, "进行中"),
    ENDED(4, "已结束");

    @EnumValue
    private final int code;
    @JsonValue
    private final String desc;

    ActivityStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
