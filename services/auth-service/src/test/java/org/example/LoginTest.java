package org.example;

import org.example.dto.ApiRequestDto;
import org.example.service.AuthService;
import org.example.constant.CacheKey;
import org.example.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class LoginTest {

    @Autowired
    private AuthService authService;

    @Test
    public void testLogin() {
        ApiRequestDto apiRequestDto = new ApiRequestDto("2897662424@qq.com", "123456");
        authService.login(apiRequestDto);
    }

    @Test
    public void testJwt() {
//        String key = CacheKey.buildCacheKey(CacheKey.USER_AUTHORITY_AREA, 123456);
//        String token = JwtUtil.generateJwtToken(key, 1000*60);
//        System.out.println(token);
//        String subject = JwtUtil.getJwtSubject(token);
//        System.out.println(subject);
//        String userId = subject.substring(subject.indexOf("::") + 2);
//        System.out.println(userId);
    }
}
