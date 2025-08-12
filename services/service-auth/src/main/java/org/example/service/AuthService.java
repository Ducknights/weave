package org.example.service;


import jakarta.annotation.Resource;
import org.example.model.AuthApiResponse;
import org.example.utils.JwtUtil;
import org.example.dto.*;
import org.example.entity.MyUserDetails;
import org.example.mapper.AuthMapper;
import org.example.model.ApiRequest;
import org.example.model.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private AuthMapper authMapper;

    public AuthApiResponse<?> login(ApiRequest apiRequest) {
        ApiResponseDto apiResponseDto = null;
        try {
            // 1. 使用Spring Security进行认证
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            apiRequest.getEmail(),
                            apiRequest.getPassword()
                    )
            );

            if (authentication.isAuthenticated()) {
                // 2. 设置认证上下文
                SecurityContextHolder.getContext().setAuthentication(authentication);
                System.out.println("authentication:" + authentication);
                // 3. 生成JWT令牌
                String subject = "UserId:" + ((MyUserDetails) authentication.getPrincipal()).getUserAuth().getId();
                String access_token = JwtUtil.generateJwtToken(subject, 1000 * 60 * 5);    // 5分钟
                String refresh_token = JwtUtil.generateJwtToken(subject, 1000 * 60 * 60 * 24);    // 24小时
                // 4. 写入用户标识信息到redis
                redisTemplate.opsForValue().set(subject, authentication.getPrincipal(), 1000 * 60 * 5, TimeUnit.MILLISECONDS);
                // 5. 构造返回DTO
                TokenDto tokenDto = new TokenDto(access_token, refresh_token, 60 * 5, 60 * 60 * 24);
                UserDto userDto = authMapper.selectUserInfo(((MyUserDetails) authentication.getPrincipal()).getUserAuth().getId());
                apiResponseDto = new ApiResponseDto(tokenDto, userDto);
            }
        } catch (Exception e) {
            return AuthApiResponse.loginFail(e.getMessage());
        }
        return AuthApiResponse.loginSuccess(apiResponseDto);
    }

    public AuthApiResponse<?> signup(ApiRequest apiRequest) {
        try {
            UserDetails user = User.builder()
                    .username(apiRequest.getEmail())
                    .password(passwordEncoder.encode( apiRequest.getPassword()))
                    .build();
            service.createUser(user);
            return AuthApiResponse.registerSuccess();
        }catch (Exception e){
            return AuthApiResponse.registerFail(e.getMessage());
        }
    }

    public AuthApiResponse<?> logout(){
        // 1. 清除redis中的用户信息
        String subject = "UserId:" + ((MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserAuth().getId();
        redisTemplate.delete(subject);
        // 2. 清除认证上下文
        SecurityContextHolder.clearContext();
        // 3. 返回注销成功信息
        return AuthApiResponse.logOutSuccess();
    }
}
