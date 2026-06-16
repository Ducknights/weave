package org.example.service;


import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.exception.CodeErrorException;
import org.example.exception.EmailExistedException;
import org.example.feign.UserFeignClient;
import org.example.dto.*;
import org.example.model.CustomUserDetails;
import org.example.mapper.AuthMapper;
import org.example.dto.ApiRequestDto;
import org.example.dto.VerifyCodeDto;
import org.example.constant.CacheKey;
import org.example.util.JwtUtil;
import org.example.util.MQUtil;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
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

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
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
    private AuthMapper authMapper;
    @Resource
    private UserFeignClient userFeignClient;
    @Resource
    private MQUtil mqUtil;
    @Resource
    private JwtUtil jwtUtil;

    public ApiResponseDto login(ApiRequestDto apiRequestDto) {
        ApiResponseDto apiResponseDto = null;
        try {
            // 使用Spring Security进行认证
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            apiRequestDto.email(),
                            apiRequestDto.password()
                    )
            );
            if (authentication.isAuthenticated()) {
                // 设置认证上下文
                SecurityContextHolder.getContext().setAuthentication(authentication);
                // 获取用户ID
                Long userId = ((CustomUserDetails) authentication.getPrincipal()).getUserId();
                // 生成Redis键
                String permissionsKey = CacheKey.buildCacheKey(CacheKey.USER_AUTHORITY, userId);
                // 生成JWT令牌
                String access_token = jwtUtil.generateJwtToken(permissionsKey, 1000 * 60 * 5); // 5分钟
                String refresh_token = jwtUtil.generateJwtToken(permissionsKey, 1000 * 60 * 60 * 24);  // 24小时
                // 写入用户标识信息到redis
                redisTemplate.opsForValue().set(permissionsKey, authentication.getPrincipal(),1, TimeUnit.HOURS); // 1小时
                // 构造返回DTO
                TokenDto tokenDto = new TokenDto(access_token, refresh_token, 60 * 5, 60 * 60 * 24);
                // 获取用户信息
                UserBriefDto userBriefDto = userFeignClient.getUserBriefById(userId);
                // 获取用户角色
                List<String> roleNames = ((CustomUserDetails) authentication.getPrincipal()).getRoles();
                UserDto userDto = new UserDto(userId, userBriefDto.getName(), userBriefDto.getAvatar(), roleNames);
                // 构建响应DTO
                apiResponseDto = new ApiResponseDto(tokenDto, userDto);
                // 发送用户登录事件
                mqUtil.sendUserLoginEvent(userId);
            }
        } catch (Exception e) {
            log.error("登录失败: {}", e.getMessage(), e);
            throw new RuntimeException("登录失败: " + e.getMessage(), e);
        }
        return apiResponseDto;
    }

    @Transactional
    public void sendCode(ApiRequestDto apiRequestDto) {
        String email = apiRequestDto.email();
        // 验证邮箱是否已存在
        if (authMapper.selectUserByEmail(email) != null){
            throw new EmailExistedException("邮箱已被注册");
        }
        // 发送验证码
        String lock = CacheKey.buildCacheKey(CacheKey.CAPTCHA, email);
        log.info("发送验证码到: {}", email);
        if (Boolean.TRUE.equals(redisTemplate.hasKey(lock))){
            throw new CodeErrorException("验证码已发送，请等待");
        }

        try{
            // 发送验证码到验证码队列
            mqUtil.sendCaptchaEmail(email);
        }catch (Exception e){
            throw new CodeErrorException("验证码发送失败");
        }
    }

    @Transactional
    public void verifyCode(VerifyCodeDto dto) {
        // 1. 验证验证码
        String key = CacheKey.buildCacheKey(CacheKey.CAPTCHA, dto.email());
        if (Boolean.FALSE.equals(redisTemplate.hasKey(key))){
            throw new CodeErrorException("验证码已过期");
        }
        Integer code = (Integer) redisTemplate.opsForValue().get(key);
        if (!dto.code().equals(code)){
            throw new CodeErrorException("验证码错误");
        }
        try {
            UserDetails user = User.builder()
                    .username(dto.email())
                    .password(passwordEncoder.encode(dto.password()))
                    .build();
            service.createUser(user);
        }catch (Exception e){
            throw new RuntimeException("注册失败", e);
        }
    }

    @Caching(evict = {
            @CacheEvict(value = CacheKey.USER_AUTHORITY, key = "#userId"),
            @CacheEvict(value = CacheKey.USER_ONLINE,key = "#userId")
    })
    public void logout(Long userId){
        try {
            SecurityContextHolder.clearContext();
            log.info("User logged out: {}", userId);
        } catch (Exception e) {
            throw new RuntimeException("Logout failed", e);
        }
    }

    public TokenDto getNewSuccessToken(Long userId) {
        try {
            // 1. 生成JWT令牌
            String subject = "UserId:" + userId;
            String access_token = jwtUtil.generateJwtToken(subject, 1000 * 60 * 5);  // 5分钟
            // 2. 构造返回DTO
            return new TokenDto(access_token, null, 60 * 5, 0);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public TokenDto getNewRefreshToken(Long userId){
        try {
            // 1. 生成JWT令牌
            String subject = "UserId:" + userId;
            String refresh_token = jwtUtil.generateJwtToken(subject, 1000 * 60 * 60 * 24);    // 24小时
            // 2. 构造返回DTO
            return new TokenDto(null, refresh_token,0, 60 * 60 * 24);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
