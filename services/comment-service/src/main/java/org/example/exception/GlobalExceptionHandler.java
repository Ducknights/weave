package org.example.exception;

import lombok.extern.log4j.Log4j2;
import org.example.model.enums.CommentApiStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 * 统一处理所有异常，返回规范的错误响应
 */
@Log4j2
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<?> handleCommentServiceException(BusinessException e) {
        log.error("业务异常:", e);
        return ResponseEntity
                .status(e.getStatus().getCode())
                .body(e.getStatus().response());
    }
    
    /**
     * 处理参数校验异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationException(MethodArgumentNotValidException e) {
        log.error("参数校验异常:", e);
        return ResponseEntity
                .badRequest()
                .body(CommentApiStatus.INVALID_PARAM.response());
    }
}
