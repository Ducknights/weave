package com.weave.chat.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum MessageType {
    TEXT(0,"text"),
    IMAGE(1,"image"),
    VIDEO(2,"video");

    @EnumValue
    private final Integer code;
    @JsonValue
    private final String type;

     MessageType(Integer code, String type) {
        this.code = code;
        this.type = type;
    }
}
