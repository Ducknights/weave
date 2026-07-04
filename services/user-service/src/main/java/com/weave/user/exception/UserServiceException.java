package com.weave.user.exception;

import lombok.Getter;
import com.weave.user.model.eunms.UserApiStatus;

/**
 * 用户服务自定义异常
 * 统一封装用户服务的业务异常
 */
@Getter
public class UserServiceException extends RuntimeException {
    
    private final int code;
    private final String message;
    
    public UserServiceException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }
    
    public UserServiceException(UserApiStatus status) {
        super(status.getMsg());
        this.code = status.getCode();
        this.message = status.getMsg();
    }
    
    public UserServiceException(UserApiStatus status, String detail) {
        super(status.getMsg() + ": " + detail);
        this.code = status.getCode();
        this.message = status.getMsg() + ": " + detail;
    }
}