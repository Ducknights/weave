package org.example.service;


import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.example.exception.CodeErrorException;
import org.example.exception.EmailExistedException;
import org.example.model.AuthApiResponse;
import org.example.dto.*;
import org.example.entity.MyUserDetails;
import org.example.mapper.AuthMapper;
import org.example.dto.ApiRequestDto;
import org.example.model.AuthApiStatus;
import org.example.dto.VerifyCodeDto;
import org.example.constant.CacheKey;
import org.example.util.JwtUtil;
import org.example.util.MQUtil;
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
    private AuthMapper authMapper;
    @Resource
    private MQUtil mqUtil;
    @Resource
    private JwtUtil jwtUtil;

    public AuthApiResponse<?> login(ApiRequestDto apiRequestDto) {
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
                Long userId = ((MyUserDetails) authentication.getPrincipal()).getUserAuth().getId();
                // 生成Redis键
                String permissionsKey = CacheKey.buildCacheKey(CacheKey.USER_AUTHORITY_AREA, userId);
                // 生成JWT令牌
                String access_token = jwtUtil.generateJwtToken(permissionsKey, 1000 * 60 * 5); // 5分钟
                String refresh_token = jwtUtil.generateJwtToken(permissionsKey, 1000 * 60 * 60 * 24);  // 24小时
                // 写入用户标识信息到redis
                redisTemplate.opsForValue().set(permissionsKey, ((MyUserDetails) authentication.getPrincipal()).getAuthorityList()); // 1小时
                // 构造返回DTO
                TokenDto tokenDto = new TokenDto(access_token, refresh_token, 60 * 5, 60 * 60 * 24);
//                UserDto userDto = userFeignClient.getUserById(userId);
                apiResponseDto = new ApiResponseDto(tokenDto, null);
            }
        } catch (Exception e) {
            System.out.println("登录失败：" + e.getMessage());
            throw new RuntimeException("登录失败", e);
        }
        return AuthApiStatus.LOGIN_SUCCESS.response(apiResponseDto);
    }

    @Transactional
    public AuthApiResponse<?> sendCode(ApiRequestDto apiRequestDto) {
        String email = apiRequestDto.email();
        // 验证邮箱是否已存在
        if (authMapper.selectUserByEmail(email) != null){
            throw new EmailExistedException("邮箱已被注册");
        }
        // 发送验证码
        String lock = "lock:" + email;
        if (Boolean.TRUE.equals(redisTemplate.hasKey(lock))){
            throw new CodeErrorException("验证码已发送，请等待");
        }
        try{
            // 发送验证码到验证码队列
            mqUtil.sendCaptchaEmail(email);
        }catch (Exception e){
            throw new CodeErrorException("验证码发送失败");
        }
        return AuthApiStatus.CODE_SEND_SUCCESS.response();
    }


    public AuthApiResponse<?> verifyCode(VerifyCodeDto dto) {
        // 1. 验证验证码
        String key = CacheKey.buildCacheKey(CacheKey.CAPTCHA_AREA, dto.email());
        String code = (String) redisTemplate.opsForValue().get(key);
        if (!dto.code().equals(code)){
            throw new CodeErrorException("验证码错误");
        }
        try {
            UserDetails user = User.builder()
                    .username(dto.email())
                    .password(passwordEncoder.encode(dto.password()))
                    .build();
            service.createUser(user);
            return AuthApiStatus.REGISTER_SUCCESS.response();
        }catch (Exception e){
            return AuthApiStatus.REGISTER_FAILED.response(e.getMessage());
        }
    }

    public AuthApiResponse<?> logout(Long userId){
        // 1. 清除redis中的用户信息
        String key= CacheKey.buildCacheKey(CacheKey.USER_AUTHORITY_AREA,userId);
        redisTemplate.delete(key);
        // 2. 清除认证上下文
        SecurityContextHolder.clearContext();
        // 3. 返回注销成功信息
        return AuthApiStatus.LOGOUT_SUCCESS.response();
    }

    public AuthApiResponse<?> getNewSuccessToken(Long userId) {
        try {
            // 1. 生成JWT令牌
            String subject = "UserId:" + userId;
            String access_token = jwtUtil.generateJwtToken(subject, 1000 * 60 * 5);    // 5分钟
            // 2. 构造返回DTO
            TokenDto tokenDto = new TokenDto(access_token, null, 60 * 5, 0);
            return AuthApiStatus.NEW_TOKEN_SUCCESS.response(tokenDto);
        } catch (Exception e) {
            return AuthApiStatus.NEW_TOKEN_FAIL.response(e.getMessage());
        }
    }

    public AuthApiResponse<?> getNewRefreshToken(Long userId){
        try {
            // 1. 生成JWT令牌
            String subject = "UserId:" + userId;
            String refresh_token = jwtUtil.generateJwtToken(subject, 1000 * 60 * 60 * 24);    // 24小时
            // 2. 构造返回DTO
            TokenDto tokenDto = new TokenDto(null, refresh_token,0, 60 * 60 * 24);
            return AuthApiStatus.NEW_TOKEN_SUCCESS.response(tokenDto);
        } catch (Exception e) {
            return AuthApiStatus.NEW_TOKEN_FAIL.response(e.getMessage());
        }
    }
}
