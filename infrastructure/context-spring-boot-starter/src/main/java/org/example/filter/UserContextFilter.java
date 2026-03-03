package org.example.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.constant.CacheKey;
import org.example.context.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class UserContextFilter extends OncePerRequestFilter {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private UserContext userContext;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {
        Long userId = null;
        try{
            userId = Long.valueOf(request.getHeader("X-UserId"));
        }catch(Exception e){
            userId = 17L;
        }
        String key = CacheKey.buildCacheKey(CacheKey.USER_AUTHORITY_AREA, userId);
        System.out.println(key);
        List<String> permissions = (List<String>) redisTemplate.opsForValue().get(key);

        userContext.setUserId(userId);
        userContext.setPermissions(permissions);

        try {
            filterChain.doFilter(request, response);
        } finally {
            userContext.clear();
        }
    }
}
