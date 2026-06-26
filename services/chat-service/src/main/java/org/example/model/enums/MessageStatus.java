package org.example.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum MessageStatus {
    UNREAD(0),
    READ(1),
    REMOVED(3);

    @EnumValue
    @JsonValue
    private final int code;

    MessageStatus(int code) {
        this.code = code;
    }
}
