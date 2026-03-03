package org.example.exception;

import lombok.extern.slf4j.Slf4j;
import org.example.dto.ErrorDto;
import org.example.model.ApiResult;
import org.example.model.ApiStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ResourceExceptionHandler{


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
                .requestId(1L)
                .timestamp(String.valueOf(System.currentTimeMillis()))
                .build();
        log.error("Error: {},Time：{},RequestId：{}", e.getMessage(), errorDto.getTimestamp(), errorDto.getRequestId());
        return ResponseEntity.status(status.getCode())
                .body(status.response(errorDto));
    }
}
