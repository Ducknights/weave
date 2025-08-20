package org.example.filter;

import com.alibaba.druid.util.StringUtils;
import io.jsonwebtoken.Claims;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.utils.JwtUtil;
import org.example.entity.MyUserDetails;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String requestUri = request.getRequestURI();

        // 1. 放行认证相关路径（使用前缀匹配）
        if (requestUri.startsWith("/api/auth/login") ||
                requestUri.startsWith("/api/auth/signup")||
        requestUri.startsWith("/api/auth/test")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // 从Token里面获取用户标识信息
            String subject = JwtUtil.getJwtToken(request);
            //  从Redis获取用户信息
            MyUserDetails userDetails = (MyUserDetails) redisTemplate.opsForValue().get(subject);
            if (userDetails == null) {
                sendUnauthorized(response, "会话过期");
                return;
            }

        } catch (Exception e) {
            System.out.println( e.getMessage());
            sendUnauthorized(response, "Invalid token: " + e.getMessage());
            return;
        }
        // 继续过滤器链
        filterChain.doFilter(request, response);
    }

    // 统一处理401响应
    private void sendUnauthorized(HttpServletResponse response, String message)
            throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\":\"" + message + "\"}");
        response.getWriter().flush();
    }
}
