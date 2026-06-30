package org.example.model.eunms;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum GenderEnum {
    FEMALE(0,"女"),
    MALE(1,"男"),
    OTHER(2,"其他");

    @EnumValue
    private final Integer code;
    @JsonValue
    private final String desc;
    GenderEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
