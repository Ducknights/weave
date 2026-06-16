package org.example;

import org.example.constant.CacheKey;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;


@SpringBootTest
public class LoginTest {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    public void testLogin() {
        String key = CacheKey.buildCacheKey(CacheKey.CAPTCHA, "2897662424@qq.com");
        redisTemplate.opsForValue().set(key, 123456);
    }
}
