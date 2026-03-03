package org.example.model;

import lombok.Getter;

@Getter
public enum GatewayStatus {

    UNAUTHORIZED(401, "未授权");

    private final int code;
    private final String msg;

    GatewayStatus(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
