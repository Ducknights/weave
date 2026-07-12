package com.weave.draft.exception;

import com.weave.draft.model.enums.DraftApiStatus;
import lombok.extern.log4j.Log4j2;
import com.weave.model.model.ApiResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.statemachine.StateMachineException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * 草稿服务全局异常处理器
 */
@Log4j2
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<?> handleBusinessException(BusinessException e) {
        log.warn("业务异常: {}", e.getStatus().getMsg());
        return ResponseEntity.status(e.getStatus().getCode())
                .body(e.getStatus().response());
    }

    @ExceptionHandler(StateMachineException.class)
    public ResponseEntity<?> handleStateMachineException(StateMachineException e) {
        log.warn("状态机异常: {}", e.getMessage());
        return ResponseEntity.badRequest().body(DraftApiStatus.INVALID_PARAM.response(e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResult<Map<String, Object>>> handleValidationException(MethodArgumentNotValidException e) {
        log.warn("参数校验失败: {}", e.getMessage());
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        Map<String, Object> data = new HashMap<>();
        data.put("errors", errors);
        return ResponseEntity.badRequest().body(DraftApiStatus.INVALID_PARAM.response(data));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception e) {
        log.error("系统异常:", e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(DraftApiStatus.SYSTEM_ERROR.response());
    }
}
