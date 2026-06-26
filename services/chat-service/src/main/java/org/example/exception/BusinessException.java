package org.example.exception;

import lombok.Getter;
import org.example.model.enums.ChatApiStatus;

@Getter
public class BusinessException extends RuntimeException{
    private final ChatApiStatus status;

    public BusinessException(ChatApiStatus status) {
        super(status.getMessage());
        this.status = status;
    }
}
