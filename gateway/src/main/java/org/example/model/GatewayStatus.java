package org.example.model;

import lombok.Getter;

@Getter
public enum GatewayStatus {

    UNAUTHORIZED(401, "未授权"),
    NO_TOKEN(401, "用户未登录，请登录后重试"),
    TOKEN_VERIFY_FAILED(401, "登录信息过期，请重新登录");

    private final int code;
    private final String msg;

    GatewayStatus(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
