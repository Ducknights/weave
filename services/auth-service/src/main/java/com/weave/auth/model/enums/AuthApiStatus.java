package com.weave.auth.model.enums;

import lombok.Getter;
import com.weave.model.model.ApiResult;
import com.weave.model.model.ApiStatus;

@Getter
public enum AuthApiStatus implements ApiStatus {
    LOGIN_SUCCESS(200,"登录成功"),
    LOGIN_FAILED(401,"登录失败"),
    REGISTER_SUCCESS(200,"注册成功"),
    REGISTER_FAILED(409,"注册失败"),
    LOGOUT_SUCCESS(200,"登出成功"),
    CODE_SEND_SUCCESS(200,"验证码发送成功" ),
    CODE_SEND_FAILED(400, "验证码发送失败"),
    NEW_TOKEN_SUCCESS(200, "新令牌获取成功"),
    NEW_TOKEN_FAIL(401, "新令牌获取失败");

    private final int code;
    private final String msg;

    AuthApiStatus(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public ApiResult<Void> response() {
        return response(null);
    }

    public <T> ApiResult<T> response(T data) {
        return new ApiResult<>(code, msg, data);
    }
}
