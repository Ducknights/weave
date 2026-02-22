package org.example.filter;

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
        requestContext.setRequestId(Long.valueOf(request.getHeader("X-RequestId")));
        requestContext.setUserId(Long.valueOf(request.getHeader("X-UserId")));
        filterChain.doFilter(request, response);
    }
}
