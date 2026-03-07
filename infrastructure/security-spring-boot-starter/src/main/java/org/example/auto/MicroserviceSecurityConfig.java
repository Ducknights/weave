package org.example.auto;

import org.example.filter.HeaderFilter;
import org.example.provider.HeaderAuthenticationProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Collections;

@Configuration
@EnableWebSecurity
public class MicroserviceSecurityConfig {

    @Bean
    @ConditionalOnMissingBean
    public HeaderFilter preAuthenticatedHeaderFilter(RedisTemplate<String, Object> redisTemplate) {
        return new HeaderFilter(redisTemplate);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           HeaderFilter headerFilter) throws Exception {
        http
                // 关闭CSRF保护
                .csrf(AbstractHttpConfigurer::disable)
                // 关闭会话管理
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 关闭默认表单登录
                .formLogin(AbstractHttpConfigurer::disable)
                // 关闭默认的注销
                .logout(AbstractHttpConfigurer::disable)
                // 允许所有请求（网关会认证）
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll())
                // 添加自定义过滤器（验证请求头）
                .addFilterBefore(headerFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(HeaderAuthenticationProvider provider) {
        return new ProviderManager(Collections.singletonList(provider));
    }

    @Bean
    public HeaderAuthenticationProvider headerAuthenticationProvider() {
        return new HeaderAuthenticationProvider();
    }
}
