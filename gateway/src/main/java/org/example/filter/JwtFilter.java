package org.example.filter;

import lombok.extern.slf4j.Slf4j;
import org.example.config.GatewayWhitelistProperties;
import org.example.exception.NoTokenException;
import org.example.exception.TokenVerifyException;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.example.util.JwtUtil;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Order(2)
@Slf4j
@Component
public class JwtFilter implements GlobalFilter {

    private final GatewayWhitelistProperties whitelistProperties;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public JwtFilter(GatewayWhitelistProperties whitelistProperties) {
        this.whitelistProperties = whitelistProperties;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String path = exchange.getRequest().getPath().value();
        log.info("请求路径: {}", path);

        if (isWhitelist(path)) {
            log.info("白名单，放行");
            return chain.filter(exchange);
        }

        String token = exchange.getRequest().getHeaders().getFirst("Authorization");

        // 1. 检查 Token 是否存在
        if (token == null || !token.startsWith("Bearer ")) {
            throw new NoTokenException("用户未登录，请登录后重试");
        }

        // 2. 去掉 "Bearer " 前缀
        String jwt = token.substring(7);

        // 3. 验证并解析 JWT
        String subject; //RedisKey
        String userId; //用户ID
        try {
            subject = JwtUtil.getJwtSubject(jwt);
            userId = subject.substring(jwt.indexOf("::") + 2);
        } catch (Exception e) {
            throw new TokenVerifyException("登录信息过期，请重新登录");
        }

        // 4. 将用户信息添加到下游请求头中
        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                .headers(headers -> headers.remove("Authorization"))
                .header("X-UserId", userId)
                .build();

        ServerWebExchange mutatedExchange = exchange.mutate().request(mutatedRequest).build();

        log.info("验证通过，放行");
        // 5. 将修改后的请求转发给下游服务
        return chain.filter(mutatedExchange);
    }

    private boolean isWhitelist(String path) {
        return whitelistProperties.getPaths().stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, path));
    }
}
