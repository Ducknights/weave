package com.weave.club.exception;

import lombok.extern.log4j.Log4j2;
import com.weave.club.model.enums.ClubApiStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 俱乐部服务异常处理器 —— 仅处理本服务特有的 IllegalArgumentException 映射，
 * 通用异常由 exception-spring-boot-starter 统一处理。
 */
@RestControllerAdvice
@Log4j2
public class ClubExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("参数非法: {}", e.getMessage());
        return ResponseEntity.status(ClubApiStatus.POST_FAIL.getCode())
                .body(ClubApiStatus.POST_FAIL.response(e.getMessage()));
    }
}
