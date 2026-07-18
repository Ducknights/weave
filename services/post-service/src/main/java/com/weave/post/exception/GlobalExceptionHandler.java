package com.weave.post.exception;

import lombok.extern.log4j.Log4j2;
import com.weave.post.model.enums.PostApiStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.statemachine.StateMachineException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 帖子服务异常处理器 —— 仅处理本服务特有的 StateMachineException，
 * 通用异常由 exception-spring-boot-starter 统一处理。
 */
@Log4j2
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(StateMachineException.class)
    public ResponseEntity<?> handleStateMachineException(StateMachineException e) {
        log.warn("状态机异常: {}", e.getMessage());
        return ResponseEntity.badRequest()
                .body(PostApiStatus.INVALID_PARAM.response(e.getMessage()));
    }
}
