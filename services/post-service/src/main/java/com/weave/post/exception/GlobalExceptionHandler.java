package com.weave.post.exception;

import lombok.extern.log4j.Log4j2;
import com.weave.model.model.ApiResult;
import com.weave.post.model.enums.PostApiStatus;
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
public class GlobalExceptionHandler {

    /**
     * 处理资源未找到异常
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFoundException(ResourceNotFoundException e) {
        log.warn("资源未找到: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(PostApiStatus.POST_NOT_FOUND.response());
    }

    /**
     * 处理授权异常
     */
    @ExceptionHandler(AuthorizationException.class)
    public ResponseEntity<?> handleAuthorizationException(AuthorizationException e) {
        log.warn("授权失败: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(PostApiStatus.PERMISSION_DENIED.response());
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
    public ResponseEntity<?> handleRuntimeException(RuntimeException e) {
        log.error("运行时异常:", e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(PostApiStatus.SYSTEM_ERROR.response());
    }
    
    /**
     * 处理所有未捕获的异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception e) {
        log.error("系统异常:", e);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(PostApiStatus.SYSTEM_ERROR.response());
    }
}