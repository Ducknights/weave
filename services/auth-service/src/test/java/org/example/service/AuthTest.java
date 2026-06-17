package org.example.service;

import org.example.constant.CacheKey;
import org.example.dto.VerifyCodeDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

@SpringBootTest
public class AuthTest {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private AuthService authService;

    /**
     * 保存验证码，用于测试注册功能
     */
    @Test
    public void testLogin() {
        String key = CacheKey.buildCacheKey(CacheKey.CAPTCHA, "2897662424@qq.com");
        redisTemplate.opsForValue().set(key, 123456, 5, TimeUnit.MINUTES);
    }

    /**
     * 直接添加一些用户
     */
    @Test
    public void testInsertUser() {
        String emailSuffix = "@qq.com";
        for (int i = 1; i < 100; i++){
            long emailHead = 2897662424L + i;
            String email = emailHead + emailSuffix;
            System.out.println(email);
            authService.register(new VerifyCodeDto(email, "123456", 123456));
        }
    }
}
