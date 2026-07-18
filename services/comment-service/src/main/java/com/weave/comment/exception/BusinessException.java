package com.weave.comment.exception;

import com.weave.exception.AbstractBusinessException;
import com.weave.comment.model.enums.CommentApiStatus;

public class BusinessException extends AbstractBusinessException {

    public BusinessException(CommentApiStatus status) {
        super(status);
    }
}
