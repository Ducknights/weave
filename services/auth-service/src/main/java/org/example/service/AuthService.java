package org.example.service;


import com.alibaba.nacos.common.utils.UuidUtils;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.example.exception.CodeErrorException;
import org.example.exception.EmailExistedException;
import org.example.feign.CaptchaFeignClient;
import org.example.feign.UserFeignClient;
import org.example.model.AuthApiResponse;
import org.example.dto.*;
import org.example.entity.MyUserDetails;
import org.example.mapper.AuthMapper;
import org.example.model.ApiRequest;
import org.example.model.VerifyCodeDto;
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

import java.util.concurrent.TimeUnit;

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
    private CaptchaFeignClient captchaFeignClient;
    @Resource
    private AuthMapper authMapper;
    @Resource
    private UserFeignClient userFeignClient;

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
                String key = "UserId:" + ((MyUserDetails) authentication.getPrincipal()).getUserAuth().getId();
                String access_token = JwtUtil.generateJwtToken(key, 1000 * 60 * 5);    // 5分钟
                String refresh_token = JwtUtil.generateJwtToken(key, 1000 * 60 * 60 * 24);    // 24小时
                // 4. 写入用户标识信息到redis
                redisTemplate.opsForValue().set(key, authentication.getPrincipal(), 1000 * 60 * 5, TimeUnit.MILLISECONDS);
                // 5. 构造返回DTO
                TokenDto tokenDto = new TokenDto(access_token, refresh_token, 60 * 5, 60 * 60 * 24);
                UserDto userDto = userFeignClient.getUserById(((MyUserDetails) authentication.getPrincipal()).getUserAuth().getId());
                apiResponseDto = new ApiResponseDto(tokenDto, userDto);
            }
        } catch (Exception e) {
            System.out.println("登录失败：" + e.getMessage());
            return AuthApiResponse.loginFail("错误码："+UuidUtils.generateUuid());
        }
        return AuthApiResponse.loginSuccess(apiResponseDto);
    }

    @Transactional
    public AuthApiResponse<?> sendCode(ApiRequest apiRequest) {
        String email = apiRequest.getEmail();
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
            captchaFeignClient.sendCaptchaCode(email);
        }catch (Exception e){
            throw new CodeErrorException("验证码发送失败");
        }
        return AuthApiResponse.codeSendSuccess();
    }

    public AuthApiResponse<?> verifyCode(VerifyCodeDto dto) {
        // 1. 验证验证码
        String code = (String) redisTemplate.opsForValue().get(dto.getEmail());
        if (!dto.getCode().equals(code)){
            throw new CodeErrorException("验证码错误");
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
        String key= "UserId:" + ((MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserAuth().getId();
        redisTemplate.delete(key);
        // 2. 清除认证上下文
        SecurityContextHolder.clearContext();
        // 3. 返回注销成功信息
        return AuthApiResponse.logOutSuccess();
    }

    /**
     * 获取新的成功令牌的方法
     * @param userId 用户ID
     * @return AuthApiResponse 包含新生成的令牌或错误信息的响应对象
     */
    public AuthApiResponse<?> getNewSuccessToken(String userId) {
        try {
            // 1. 生成JWT令牌
            // 构造JWT主题，包含用户ID信息
            String subject = "UserId:" + userId;
            // 生成有效期为5分钟的JWT访问令牌
            String access_token = JwtUtil.generateJwtToken(subject, 1000 * 60 * 5);    // 5分钟
            // 2. 构造返回DTO
            // 创建令牌对象，包含访问令牌、刷新令牌(此处为null)、有效期(5分钟)和过期时间(0)
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
