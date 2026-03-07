package org.example.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.example.constant.CacheKey;
import org.example.constant.RequestHeader;
import org.example.dto.HeaderTokenDto;
import org.example.model.CustomUserDetails;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
public class Filter extends OncePerRequestFilter {

    private final RedisTemplate<String, Object> redisTemplate;

    public Filter(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        if( request.getRequestURI().equals("/login")||
            request.getRequestURI().equals("/register")){
            log.debug("登录请求，不进行用户认证");
            return;
        }
        String userIdStr = request.getHeader(RequestHeader.X_USER_ID);
        if (userIdStr != null) {
            try {
                Long userId = Long.valueOf(userIdStr);
                String key = CacheKey.buildCacheKey(CacheKey.USER_AUTHORITY_AREA, userId);
                
                CustomUserDetails userDetails = (CustomUserDetails) redisTemplate.opsForValue().get(key);
                
                if (userDetails != null) {
                    HeaderTokenDto token = new HeaderTokenDto(userDetails);
                    SecurityContextHolder.getContext().setAuthentication(token);
                    log.debug("Redis中获取用户信息: userId={}, username={}", userId, userDetails.getUsername());
                } else {
                    log.warn("Redis中没有用户信息: {}", userId);
                    SecurityContextHolder.clearContext();
                }
            } catch (NumberFormatException e) {
                log.warn("Redis中无效的X-UserId header: {}", userIdStr);
                SecurityContextHolder.clearContext();
            } catch (Exception e) {
                log.error("Redis中用户认证错误", e);
                SecurityContextHolder.clearContext();
            }
        } else {
            SecurityContextHolder.clearContext();
        }
        
        chain.doFilter(request, response);
    }
}
