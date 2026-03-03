package org.example.model;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum InteractionEnum {
    LIKE(1,"点赞"),
    FAVORITE(2,"收藏"),
    SHARE(3,"分享"),
    FOLLOW(4,"关注"),
    MUTE(5,"屏蔽"),
    BLOCK(6,"拉黑");

    @EnumValue
    private final int code;
    @JsonValue
    private final String description;

    InteractionEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }
}
