package com.weave.chat.exception;

import com.weave.exception.AbstractBusinessException;
import com.weave.chat.model.enums.ChatApiStatus;

public class BusinessException extends AbstractBusinessException {

    public BusinessException(ChatApiStatus status) {
        super(status);
    }
}
