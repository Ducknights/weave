package org.example.filter;

import io.jsonwebtoken.Claims;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.entity.MyUserDetails;
import org.example.utils.JwtUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

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
            System.out.println(e.getMessage());
            sendUnauthorized(response, "错误信息: " + e.getMessage());
            return;
        }
        // 继续过滤器链
        filterChain.doFilter(request, response);
    }

    // 统一处理401响应
    private void sendUnauthorized(HttpServletResponse response, String message)
            throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"error\":\"" + message + "\"}");
        response.getWriter().flush();
    }
}
