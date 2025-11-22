package org.example.exception;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.example.bean.RequestContext;
import org.example.dto.ErrorDto;
import org.example.model.ApiStatus;
import org.example.model.ResourcesApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @Resource
    private RequestContext requestContext;

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ResourcesApiResponse<?>> handleBusinessException(Exception e) {
        ErrorDto errorDto = ErrorDto.builder()
                .requestId(requestContext.getRequestId())
                .timestamp(String.valueOf(System.currentTimeMillis()))
                .build();
        log.error("Error: {},Time：{}", e.getMessage(), errorDto.getTimestamp());
        return ResponseEntity.status(ApiStatus.ERROR.getCode())
                .body(ResourcesApiResponse.error(errorDto));
    }
}
