package com.weave.club.exception;

import com.weave.exception.AbstractBusinessException;
import com.weave.club.model.enums.ClubApiStatus;

public class BusinessException extends AbstractBusinessException {

    public BusinessException(ClubApiStatus status) {
        super(status);
    }
}
