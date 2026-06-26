package org.example.exception;

import lombok.extern.log4j.Log4j2;
import org.example.model.ApiResult;
import org.example.model.enums.ChatApiStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Log4j2
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResult<?>> handleBusinessException(BusinessException e) {
        log.error("业务异常: {}", e.getMessage());
        return ResponseEntity
                .status(e.getStatus().getCode())
                .body(e.getStatus().response());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResult<?>> handleException(Exception e) {
        log.error("系统异常: ", e);
        return ResponseEntity
                .status(500)
                .body(ChatApiStatus.INTERNAL_SERVER_ERROR.response());
    }
}
