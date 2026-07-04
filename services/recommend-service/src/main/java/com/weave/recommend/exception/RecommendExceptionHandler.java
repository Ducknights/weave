package com.weave.recommend.exception;

import lombok.extern.log4j.Log4j2;
import com.weave.model.model.ApiResult;
import com.weave.recommend.model.enums.RecommendApiStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Log4j2
@RestControllerAdvice
public class RecommendExceptionHandler {

    @ExceptionHandler(RecommendServiceException.class)
    public ResponseEntity<?> handleRecommendServiceException(RecommendServiceException e) {
        log.warn("推荐服务业务异常: code={}, message={}", e.getCode(), e.getMessage());

        ApiResult<Map<String, Object>> result = new ApiResult<>(
                e.getCode(),
                e.getMessage(),
                null
        );
        return ResponseEntity.status(e.getCode()).body(result);
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

        ApiResult<Map<String, Object>> result = RecommendApiStatus.INVALID_PARAM.response(data);
        return ResponseEntity.badRequest().body(result);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResult<Map<String, Object>>> handleRuntimeException(RuntimeException e) {
        log.error("推荐服务运行时异常:", e);

        ApiResult<Map<String, Object>> result = RecommendApiStatus.SYSTEM_ERROR.response();
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(result);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResult<Map<String, Object>>> handleException(Exception e) {
        log.error("推荐服务系统异常:", e);

        ApiResult<Map<String, Object>> result = RecommendApiStatus.SYSTEM_ERROR.response();
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(result);
    }
}
