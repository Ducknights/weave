package org.example.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

@Getter
public enum MemberStatus {
    ACTIVE(1), // 激活
    INACTIVE(0), // 禁用
    PENDING(2); // 待处理

    @EnumValue
    private final Integer code;
    MemberStatus(Integer code) {
        this.code = code;
    }
}
