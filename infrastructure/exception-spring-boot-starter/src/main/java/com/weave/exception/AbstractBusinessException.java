package com.weave.exception;

import com.weave.model.model.ApiStatus;
import lombok.Getter;

/**
 * 业务异常基类 —— 各服务通过继承绑定自己的 *ApiStatus 枚举，由 starter 的 GlobalExceptionHandler 统一处理。
 */
@Getter
public abstract class AbstractBusinessException extends RuntimeException {

    private final ApiStatus status;

    protected AbstractBusinessException(ApiStatus status) {
        super(status.getMsg());
        this.status = status;
    }
}
