package com.weave.draft.exception;

import com.weave.exception.AbstractBusinessException;
import com.weave.draft.model.enums.DraftApiStatus;

public class BusinessException extends AbstractBusinessException {

    public BusinessException(DraftApiStatus status) {
        super(status);
    }
}
