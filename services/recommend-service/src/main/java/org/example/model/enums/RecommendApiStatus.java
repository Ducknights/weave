package org.example.model.enums;

import lombok.Getter;
import org.example.model.ApiResult;

import java.util.Collections;
import java.util.Map;

@Getter
public enum RecommendApiStatus {
    SUCCESS(200, "成功"),
    RECOMMEND_SUCCESS(200, "推荐成功"),
    
    INVALID_PARAM(400, "参数无效"),
    MISSING_USER_ID(400, "缺少用户ID"),
    
    UNAUTHORIZED(401, "未授权"),
    
    SYSTEM_ERROR(500, "系统错误"),
    RECOMMEND_FAILED(500, "推荐失败");

    private final int code;
    private final String msg;

    RecommendApiStatus(int code, String msg) {
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
