package org.example.exception;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.bean.RequestContext;
import org.example.dto.ErrorDto;
import org.example.model.ApiResult;
import org.example.model.ApiStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ResourceExceptionHandler{

    @Resource
    private RequestContext requestContext;

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResult<?>>handleBusinessException(Exception e) {
        return buildErrorResponse(ApiStatus.ERROR, e);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResult<?>>handleFileNotFoundException(IllegalArgumentException e) {
        return buildErrorResponse(ApiStatus.POST_FAIL, e);
    }

    @ExceptionHandler(MinioException.class)
    public ResponseEntity<ApiResult<?>>handleFileNotFoundException(MinioException e) {
        return buildErrorResponse(ApiStatus.ERROR, e);
    }

    private ResponseEntity<ApiResult<?>> buildErrorResponse(ApiStatus status, Exception e) {
        ErrorDto errorDto = ErrorDto.builder()
                .message(e.getMessage())
                .requestId(requestContext.getRequestId())
                .timestamp(String.valueOf(System.currentTimeMillis()))
                .build();
        log.error("Error: {},Time：{},RequestId：{}", e.getMessage(), errorDto.timestamp(), errorDto.requestId());
        return ResponseEntity.status(status.getCode())
                .body(status.response(errorDto));
    }
}
