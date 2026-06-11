package org.example.exception;

import lombok.extern.slf4j.Slf4j;
import org.example.model.GatewayResponse;
import org.example.model.GatewayStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@RestControllerAdvice
public class GatewayExceptionHandler {

    @ExceptionHandler(NoTokenException.class)
    public Mono<ResponseEntity<GatewayResponse<?>>> handleNoTokenException(
            NoTokenException e, ServerWebExchange exchange) {
        log.warn("未携带Token: {}", exchange.getRequest().getPath());
        return buildResponse(GatewayStatus.NO_TOKEN);
    }

    @ExceptionHandler(TokenVerifyException.class)
    public Mono<ResponseEntity<GatewayResponse<?>>> handleTokenVerifyException(
            TokenVerifyException e, ServerWebExchange exchange) {
        log.warn("Token验证失败: {}", exchange.getRequest().getPath());
        return buildResponse(GatewayStatus.TOKEN_VERIFY_FAILED);
    }

    private Mono<ResponseEntity<GatewayResponse<?>>> buildResponse(GatewayStatus status) {
        GatewayResponse<?> response = new GatewayResponse<>(status.getCode(), status.getMsg(), null);
        return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response));
    }
}
