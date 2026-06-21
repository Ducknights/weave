package org.example.exception;

import lombok.Getter;
import org.example.model.GatewayStatus;

@Getter
public class BusinessException extends RuntimeException{
    private final GatewayStatus status;

    public BusinessException(GatewayStatus status) {
        super(status.getMsg());
        this.status = status;
    }
}
