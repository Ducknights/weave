package org.example.exception;

import lombok.Getter;
import org.example.model.enums.RecommendApiStatus;

@Getter
public class RecommendServiceException extends RuntimeException {

    private final int code;
    private final String message;

    public RecommendServiceException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public RecommendServiceException(RecommendApiStatus status) {
        super(status.getMsg());
        this.code = status.getCode();
        this.message = status.getMsg();
    }

    public RecommendServiceException(RecommendApiStatus status, String detail) {
        super(status.getMsg() + ": " + detail);
        this.code = status.getCode();
        this.message = status.getMsg() + ": " + detail;
    }
}
