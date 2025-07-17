package org.example.filter;

import com.alibaba.druid.util.StringUtils;
import io.jsonwebtoken.Claims;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.entity.MyUserDetails;
import org.example.utils.JwtUtil;
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
        if (requestUri.startsWith("/api/auth/")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. 安全获取Authorization头
        String authHeader = request.getHeader("Authorization");

        // 3. 验证Header格式
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendUnauthorized(response, "Missing or invalid Authorization header");
            return;
        }

        // 4. 提取Token
        String token = authHeader.substring(7);

        if (StringUtils.isEmpty(token)) {
            sendUnauthorized(response, "Empty token");
            return;
        }

        try {
            // 5. 解析Token
            Claims claims = JwtUtil.parseJwtToken(token);
            String subject = claims.getSubject();
            System.out.println(subject);

            // 6. 从Redis获取用户信息
            MyUserDetails userDetails = (MyUserDetails) redisTemplate.opsForValue().get(subject);
            System.out.println(userDetails);
            if (userDetails == null) {
                sendUnauthorized(response, "Session expired");
                return;
            }

            // 7. 创建认证对象
            Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, null);

            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (Exception e) {
            sendUnauthorized(response, "Invalid token: " + e.getMessage());
            return;
        }
        System.out.println("1");
        // 8. 继续过滤器链
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
