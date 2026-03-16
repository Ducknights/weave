package org.example.model;

import lombok.Getter;

import java.util.Collections;
import java.util.Map;

@Getter
public enum ApiStatus {
    GET_SUCCESS(200, "请求成功"),
    GET_FAIL(400, "请求失败"),
    POST_SUCCESS(201, "创建成功"),
    POST_FAIL(400, "创建失败"),
    PUT_SUCCESS(200, "更新成功"),
    PUT_FAIL(400, "更新失败"),
    DELETE_SUCCESS(200, "删除成功"),
    DELETE_FAIL(400, "删除失败"),
    ERROR(500, "运行错误");

    private final int code;
    private final String msg;

    ApiStatus(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public ApiResult<Map<String, Object>> response() {
        return response(Collections.emptyMap());
    }

    public <T> ApiResult<T> response(T data) {
        return new ApiResult<>(code, msg, data);
    }
}
