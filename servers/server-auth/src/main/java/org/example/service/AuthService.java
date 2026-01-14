package org.example.service;


import com.alibaba.nacos.common.utils.UuidUtils;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.example.exception.EmailExistedException;
import org.example.model.AuthApiResponse;
import org.example.dto.*;
import org.example.entity.MyUserDetails;
import org.example.mapper.AuthMapper;
import org.example.model.ApiRequest;
import org.example.model.RegisterPart2Dto;
import org.example.util.JwtUtil;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.example.config.RabbitMQConfig.*;

@Log4j2
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
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
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
                apiResponseDto = new ApiResponseDto(tokenDto, null);
            }
        } catch (Exception e) {
            System.out.println("登录失败：" + e.getMessage());
            return AuthApiResponse.loginFail("错误码："+UuidUtils.generateUuid());
        }
        return AuthApiResponse.loginSuccess(apiResponseDto);
    }

    @Transactional
    public AuthApiResponse<?> signup(ApiRequest apiRequest) {
        if (authMapper.selectUserByEmail(apiRequest.getEmail()) != null){
            throw new EmailExistedException("邮箱已被注册");
        }
        rabbitTemplate.convertAndSend(TOPIC_EXCHANGE, CAPTCHA_ROUTING_KEY, apiRequest.getEmail());
        //todo 这里应该返回 “验证码发送成功”
        return AuthApiResponse.registerSuccess();
    }

    public AuthApiResponse<?> register(RegisterPart2Dto dto) {
        // 1. 验证验证码
        String code = (String) redisTemplate.opsForValue().get(dto.getEmail());
        if (!dto.getCode().equals(code)){
            //todo 抛出“验证码错误异常”
            return AuthApiResponse.registerFail("验证码错误");
        }
        try {
            UserDetails user = User.builder()
                    .username(dto.getEmail())
                    .password(passwordEncoder.encode(dto.getPassword()))
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

    public AuthApiResponse<?> getNewSuccessToken(String userId) {
        try {
            // 1. 生成JWT令牌
            String subject = "UserId:" + userId;
            String access_token = JwtUtil.generateJwtToken(subject, 1000 * 60 * 5);    // 5分钟
            // 2. 构造返回DTO
            TokenDto tokenDto = new TokenDto(access_token, null, 60 * 5, 0);
            return AuthApiResponse.getNewTokenSuccess(tokenDto);
        } catch (Exception e) {
            return AuthApiResponse.getNewTokenFail(e.getMessage());
        }
    }

    public AuthApiResponse<?> getNewRefreshToken(String userId){
        try {
            // 1. 生成JWT令牌
            String subject = "UserId:" + userId;
            String refresh_token = JwtUtil.generateJwtToken(subject, 1000 * 60 * 60 * 24);    // 24小时
            // 2. 构造返回DTO
            TokenDto tokenDto = new TokenDto(null, refresh_token,0, 60 * 60 * 24);
            return AuthApiResponse.getNewTokenSuccess(tokenDto);
        } catch (Exception e) {
            return AuthApiResponse.getNewTokenFail(e.getMessage());
        }
    }
}
