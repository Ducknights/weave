package org.example.filter;


import lombok.extern.slf4j.Slf4j;
import org.example.util.IdUtil;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Order(1)
@Component
public class RequestIdFilter implements GlobalFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        final Long snowflake = IdUtil.snowflakeId(1, 1);
        log.info("X-RequestId: {}", snowflake);
        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                .header("X-RequestId", String.valueOf(snowflake))
                .build();
        ServerWebExchange mutatedExchange = exchange.mutate().request(mutatedRequest).build();

        return chain.filter(mutatedExchange);
    }
}
