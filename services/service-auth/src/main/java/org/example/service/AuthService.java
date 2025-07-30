package org.example.service;


import io.lettuce.core.ScriptOutputType;
import jakarta.annotation.Resource;
import org.example.dto.AuthResponse;
import org.example.dto.AuthRequest;
import org.example.entity.MyUserDetails;
import org.example.utils.JwtUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

import static org.example.dto.AuthResponse.*;

@Service
public class AuthService {
    @Resource
    private AuthenticationManager authenticationManager;
    @Resource
    private SecurityUserDetailsService service;
    @Resource
    private PasswordEncoder passwordEncoder;
    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    public AuthResponse<?> login(AuthRequest authRequest) {
        try {
            // 1. 使用Spring Security进行认证
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequest.getEmail(),
                            authRequest.getPassword()
                    )
            );

            String access_token = "";
            String refresh_token = "";
            if (authentication.isAuthenticated()) {
                // 2. 设置认证上下文
                SecurityContextHolder.getContext().setAuthentication(authentication);
                System.out.println(authentication);
                // 3. 生成JWT令牌
                String subject = "UserId:" + ((MyUserDetails) authentication.getPrincipal()).getUserAuth().getId();
                access_token = JwtUtil.generateJwtToken(subject, 1000 * 60 * 5);    // 5分钟
                refresh_token = JwtUtil.generateJwtToken(subject, 1000 * 60 * 60 * 24);    // 24小时
                // 4. 写入用户标识信息到redis
                redisTemplate.opsForValue().set(subject, authentication.getPrincipal(), 1000 * 60 * 5, TimeUnit.MILLISECONDS);
            }
            return authSuccess(access_token, "成功",refresh_token);
        } catch (Exception e) {
            return authFail(401, "失败");
        }
    }

    public AuthResponse<?> signup(AuthRequest authRequest) {
        try {
            UserDetails user = User.builder()
                    .username(authRequest.getEmail())
                    .password(passwordEncoder.encode( authRequest.getPassword()))
                    .build();
            service.createUser(user);
            return signSuccess("成功");
        }catch (Exception e){
            return signFail(409,"失败");
        }
    }

    public AuthResponse<?> logout(){
        // 1. 清除redis中的用户信息
        String subject = "UserId:" + ((MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserAuth().getId();
        redisTemplate.delete(subject);
        // 2. 清除认证上下文
        SecurityContextHolder.clearContext();
        // 3. 返回注销成功信息
        return logoutSuccess("注销成功");
    }
}
