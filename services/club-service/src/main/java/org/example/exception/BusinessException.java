package org.example.exception;

import lombok.Getter;
import org.example.model.enums.ClubApiStatus;

@Getter
public class BusinessException extends RuntimeException {
    private final ClubApiStatus status;

    public BusinessException(ClubApiStatus status) {
        super(status.getMsg());
        this.status = status;
    }
}
