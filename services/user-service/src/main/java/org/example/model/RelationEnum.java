package org.example.model;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 *
 * 关系枚举类，表示用户之间的关系，如关注、屏蔽、拉黑等。
 */

@Getter
public enum RelationEnum {
    FOLLOW(1,"关注"),
    MUTE(2,"屏蔽"),
    BLOCK(3,"拉黑");

    @EnumValue
    private final int code;
    @JsonValue
    private final String desc;

    RelationEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}

