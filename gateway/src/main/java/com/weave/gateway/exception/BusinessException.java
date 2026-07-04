package com.weave.gateway.exception;

import com.weave.gateway.model.GatewayStatus;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException{
    private final GatewayStatus status;

    public BusinessException(GatewayStatus status) {
        super(status.getMsg());
        this.status = status;
    }
}
