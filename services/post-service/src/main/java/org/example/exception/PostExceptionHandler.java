package org.example.exception;

import lombok.extern.log4j.Log4j2;
import org.example.model.ApiResult;
import org.example.model.enums.PostApiStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.statemachine.StateMachineException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * 帖子服务全局异常处理器
 * 统一处理所有异常，返回规范的错误响应
 */
@Log4j2
@RestControllerAdvice
public class PostExceptionHandler {
    
    /**
     * 处理业务异常
     */
    @ExceptionHandler(PostServiceException.class)
    public ResponseEntity<ApiResult<Map<String, Object>>> handlePostServiceException(PostServiceException e) {
        log.warn("业务异常: code={}, message={}", e.getCode(), e.getMessage());
        
        ApiResult<Map<String, Object>> result = new ApiResult<>(
                e.getCode(),
                e.getMessage(),
                null
        );
        
        HttpStatus status = HttpStatus.valueOf(e.getCode());
        return ResponseEntity.status(status).body(result);
    }

    /**
     * 处理状态机异常（非法状态转换）
     */
    @ExceptionHandler(StateMachineException.class)
    public ResponseEntity<?> handleStateMachineException(StateMachineException e) {
        log.warn("状态机异常: {}", e.getMessage());
        return ResponseEntity.badRequest().body(PostApiStatus.INVALID_PARAM.response(e.getMessage()));
    }
    
    /**
     * 处理参数校验异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResult<Map<String, Object>>> handleValidationException(MethodArgumentNotValidException e) {
        log.warn("参数校验失败: {}", e.getMessage());
        
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error -> 
                errors.put(error.getField(), error.getDefaultMessage())
        );
        
        Map<String, Object> data = new HashMap<>();
        data.put("errors", errors);
        
        ApiResult<Map<String, Object>> result = PostApiStatus.INVALID_PARAM.response(data);
        return ResponseEntity.badRequest().body(result);
    }
    
    /**
     * 处理运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResult<Map<String, Object>>> handleRuntimeException(RuntimeException e) {
        log.error("运行时异常:", e);
        
        ApiResult<Map<String, Object>> result = PostApiStatus.SYSTEM_ERROR.response();
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(result);
    }
    
    /**
     * 处理所有未捕获的异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResult<Map<String, Object>>> handleException(Exception e) {
        log.error("系统异常:", e);
        
        ApiResult<Map<String, Object>> result = PostApiStatus.SYSTEM_ERROR.response();
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(result);
    }
}