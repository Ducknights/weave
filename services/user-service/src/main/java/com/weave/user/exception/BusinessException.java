package com.weave.user.exception;

import com.weave.exception.AbstractBusinessException;
import com.weave.user.model.eunms.UserApiStatus;

public class BusinessException extends AbstractBusinessException {

    public BusinessException(UserApiStatus status) {
        super(status);
    }
}
