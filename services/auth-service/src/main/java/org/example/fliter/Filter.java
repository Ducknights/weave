package org.example.fliter;

import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.bean.RequestContext;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@Component
public class Filter extends OncePerRequestFilter {

    @Resource
    private RequestContext requestContext;

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestIdStr = request.getHeader("X-RequestId");
        String userIdStr = request.getHeader("X-UserId");
        
        if (requestIdStr != null && !requestIdStr.trim().isEmpty()) {
            requestContext.setRequestId(Long.valueOf(requestIdStr));
        } else {
            requestContext.setRequestId(null); // 或设置默认值 0L
        }
        
        if (userIdStr != null && !userIdStr.trim().isEmpty()) {
            requestContext.setUserId(Long.valueOf(userIdStr));
        } else {
            requestContext.setUserId(null); // 或设置默认值 0L
        }
        
        filterChain.doFilter(request, response);
    }
}
