package com.weave.post.exception;


import com.weave.post.model.enums.PostApiStatus;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final PostApiStatus status;

    public BusinessException(PostApiStatus status) {
        this.status = status;
    }
}
