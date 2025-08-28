package org.example.model;


import lombok.Getter;

@Getter
public enum ApiStatus {
    LOGIN_SUCCESS(200,"登录成功"),
    LOGIN_FAILED(401,"登录失败"),
    REGISTER_SUCCESS(200,"注册成功"),
    REGISTER_FAILED(409,"注册失败"),
    PASSWORD_RESET_SUCCESS(200,"密码重置成功"),
    PASSWORD_RESET_FAILED(400,"密码重置失败");

    private final int code;
    private final String msg;

    ApiStatus(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
