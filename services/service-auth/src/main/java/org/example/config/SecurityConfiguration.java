package org.example.config;

import jakarta.annotation.Resource;
import org.example.filter.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;


@Configuration
public class SecurityConfiguration {

    @Resource
    private UserDetailsService service;
    @Resource
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(service);
        provider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(provider);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        //自定义配置
        http
                .authorizeHttpRequests((auth) -> auth
                        //排除认证链接
                        .requestMatchers("/api/auth/login").permitAll()
                        .requestMatchers("/api/auth/signup").permitAll()
                        .anyRequest().authenticated())
                //关闭CSRF
                .csrf(AbstractHttpConfigurer::disable)
                //添加JWT过滤器
                .addFilterBefore(jwtAuthenticationFilter, AuthorizationFilter.class)
                //允许跨域
                .cors(Customizer.withDefaults())
                //关闭session管理
                .sessionManagement(AbstractHttpConfigurer::disable)
                //关闭默认的表单登录
                .formLogin(AbstractHttpConfigurer::disable)
                //关闭默认的注销
                .logout(AbstractHttpConfigurer::disable);

        //返回新的过滤器链
        return http.build();
    }
}
