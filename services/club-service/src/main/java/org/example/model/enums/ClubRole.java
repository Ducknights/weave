package org.example.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum ClubRole {
    OFFICER(1),
    MEMBER(2);

    @EnumValue
    private final int code;

    ClubRole(int code) {
        this.code = code;
    }
}
