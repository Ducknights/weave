package com.weave.recommend.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum ActionEnum {
    LIKE(1, "点赞", 3),
    COLLECT(2, "收藏", 5),
    VIEW(3, "浏览", 1);

    @EnumValue
    private final int code;
    @JsonValue
    private final String desc;
    private final int weight;

    ActionEnum(int code, String desc, int weight) {
        this.code = code;
        this.desc = desc;
        this.weight = weight;
    }
}
