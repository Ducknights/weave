package com.weave.gateway.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

@Slf4j
@RestControllerAdvice
public class GatewayExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public Mono<ResponseEntity<?>> handleTokenVerifyException(
            BusinessException e) {
        log.warn("业务异常: {}", e.getMessage());
        return Mono.just(ResponseEntity
                .status(e.getStatus().getCode())
                .body(e.getStatus().response()));
    }
}
