package org.example.exception;

import lombok.Getter;
import org.example.model.enums.CommentApiStatus;

@Getter
public class BusinessException  extends RuntimeException {
    private final CommentApiStatus status;

    public BusinessException(CommentApiStatus status) {
        super(status.getMsg());
        this.status = status;
    }
}
