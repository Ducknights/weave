package com.weave.post.exception;

import com.weave.exception.AbstractBusinessException;
import com.weave.post.model.enums.PostApiStatus;

public class BusinessException extends AbstractBusinessException {

    public BusinessException(PostApiStatus status) {
        super(status);
    }
}
