package org.example.filter;

import lombok.extern.slf4j.Slf4j;
import org.example.config.GatewayWhitelistProperties;
import org.example.constant.RequestHeader;
import org.example.exception.BusinessException;
import org.example.model.GatewayStatus;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.example.util.JwtUtil;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Order(2)
@Slf4j
@Component
public class JwtFilter implements GlobalFilter {

    private final GatewayWhitelistProperties whitelistProperties;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    private List<GatewayWhitelistProperties.WhitelistEntry> whitelistEntries;

    public JwtFilter(GatewayWhitelistProperties whitelistProperties) {
        this.whitelistProperties = whitelistProperties;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String path = exchange.getRequest().getPath().value();
        HttpMethod httpMethod = exchange.getRequest().getMethod();
        log.info("请求路径: {}, 方法: {}", path, httpMethod);

        if (isWhitelist(path, httpMethod)) {
            log.info("白名单，放行");
            return chain.filter(exchange);
        }

        String token = exchange.getRequest().getHeaders().getFirst(RequestHeader.AUTHORIZATION);

        // 1. 检查 Token 是否存在
        if (token == null || !token.startsWith(RequestHeader.BEARER)) {
            throw new BusinessException(GatewayStatus.NO_TOKEN);
        }

        // 2. 去掉 "Bearer " 前缀
        String JWT = token.substring(RequestHeader.BEARER.length());

        // 3. 验证并解析 JWT
        String subject;
        String userId;
        try {
            subject = JwtUtil.getJwtSubject(JWT);
            log.info("用户标识信息: {}", subject);
            userId = subject.substring(subject.indexOf("::") + 2);
        } catch (Exception e) {
            throw new BusinessException(GatewayStatus.TOKEN_VERIFY_FAILED);
        }

        // 4. 将用户信息添加到下游请求头中
        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                .headers(headers -> headers.remove(RequestHeader.AUTHORIZATION))
                .header(RequestHeader.X_USER_ID, userId)
                .build();

        ServerWebExchange mutatedExchange = exchange.mutate().request(mutatedRequest).build();

        log.info("验证通过，放行");
        // 5. 将修改后的请求转发给下游服务
        return chain.filter(mutatedExchange);
    }

    private boolean isWhitelist(String path, HttpMethod httpMethod) {
        if (whitelistEntries == null) {
            whitelistEntries = whitelistProperties.parseEntries();
        }
        return whitelistEntries.stream()
                .anyMatch(entry -> {
                    boolean methodMatch = entry.method() == null || entry.method() == httpMethod;
                    boolean pathMatch = pathMatcher.match(entry.pathPattern(), path);
                    return methodMatch && pathMatch;
                });
    }
}
