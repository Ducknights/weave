package com.weave.chat.exception;

import com.weave.chat.model.enums.ChatApiStatus;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException{
    private final ChatApiStatus status;

    public BusinessException(ChatApiStatus status) {
        super(status.getMessage());
        this.status = status;
    }
}
