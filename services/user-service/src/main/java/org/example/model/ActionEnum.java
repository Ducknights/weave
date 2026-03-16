package org.example.model;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;


/**
 * 动作枚举类，表示用户可能执行的动作，如点赞、收藏、分享等。
 */

@Getter
public enum ActionEnum {
    LIKE(1,"点赞"),
    FAVORITE(2,"收藏"),
    SHARE(3,"分享");

    @EnumValue
    private final int code;
    @JsonValue
    private final String desc;

    ActionEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
