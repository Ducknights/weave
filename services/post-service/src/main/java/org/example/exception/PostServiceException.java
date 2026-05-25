package org.example.exception;

import lombok.Getter;
import org.example.model.enums.PostApiStatus;

/**
 * 帖子服务自定义异常
 */
@Getter
public class PostServiceException extends RuntimeException {
    
    private final int code;
    private final String message;
    
    public PostServiceException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }
    
    public PostServiceException(PostApiStatus status) {
        super(status.getMsg());
        this.code = status.getCode();
        this.message = status.getMsg();
    }
    
    public PostServiceException(PostApiStatus status, String detail) {
        super(status.getMsg() + ": " + detail);
        this.code = status.getCode();
        this.message = status.getMsg() + ": " + detail;
    }
}