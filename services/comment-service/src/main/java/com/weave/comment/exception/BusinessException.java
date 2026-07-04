package com.weave.comment.exception;

import com.weave.comment.model.enums.CommentApiStatus;
import lombok.Getter;

@Getter
public class BusinessException  extends RuntimeException {
    private final CommentApiStatus status;

    public BusinessException(CommentApiStatus status) {
        super(status.getMsg());
        this.status = status;
    }
}
