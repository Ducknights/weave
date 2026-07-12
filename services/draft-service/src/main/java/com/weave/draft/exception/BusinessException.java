package com.weave.draft.exception;

import com.weave.draft.model.enums.DraftApiStatus;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final DraftApiStatus status;

    public BusinessException(DraftApiStatus status) {
        this.status = status;
    }
}
