package com.weave.club.exception;

import com.weave.club.model.enums.ClubApiStatus;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final ClubApiStatus status;

    public BusinessException(ClubApiStatus status) {
        super(status.getMsg());
        this.status = status;
    }
}
