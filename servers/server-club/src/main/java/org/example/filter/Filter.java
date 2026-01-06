package org.example.filter;

import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.example.bean.RequestContext;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Log4j2
@Component
public class Filter extends OncePerRequestFilter {

    @Resource
    private RequestContext requestContext;

    @Override
    protected void doFilterInternal(HttpServletRequest request,HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        requestContext.setRequestId(request.getHeader("X-RequestId"));
        requestContext.setUserId(request.getHeader("X-UserId"));
        log.info("request: {}", request.getRequestURI());
        filterChain.doFilter(request, response);
    }
}
