package org.example.exception;

import lombok.Getter;
import org.example.model.enums.CommentApiStatus;

/**
 * 评论服务自定义异常
 */
@Getter
public class CommentServiceException extends RuntimeException {
    
    private final int code;
    private final String message;
    
    public CommentServiceException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }
    
    public CommentServiceException(CommentApiStatus status) {
        super(status.getMsg());
        this.code = status.getCode();
        this.message = status.getMsg();
    }
    
    public CommentServiceException(CommentApiStatus status, String detail) {
        super(status.getMsg() + ": " + detail);
        this.code = status.getCode();
        this.message = status.getMsg() + ": " + detail;
    }
}
