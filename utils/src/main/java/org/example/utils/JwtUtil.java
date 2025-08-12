package org.example.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

@Component
public class JwtUtil {
    @Autowired
    private static RedisTemplate<String, Object> redisTemplate;

    private static final String SECRET = "ChenXin";

    //生成jwt
    public static String generateJwtToken(String subject,int expiration) {
        return Jwts.builder()
                //头部
                .setHeaderParam("typ", "JWT")
                .setHeaderParam("alg", "HS256")
                //载荷
                .setId(UUID.randomUUID().toString())//唯一id
                .setSubject(subject)//主题
                .setIssuer("TianYongCheng")//签发人
                .setIssuedAt(new Date())//签发时间
                .setExpiration(new Date(System.currentTimeMillis() + expiration))//过期时间
                //签名
                .signWith(io.jsonwebtoken.SignatureAlgorithm.HS256, SECRET)
                .compact();
    }

    public static String getJwtToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
            //验证Header格式
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new RuntimeException("请求头中的授权信息格式不正确或缺失");
            }

            // 提取Token
            String token = authHeader.substring(7);

            if (token.isEmpty()) {
                throw new RuntimeException("token为空");
            }
            // 解析Token得到用户标识信息
            Claims claims = JwtUtil.parseJwtToken(token);
        return claims.getSubject();
    }

    //解析jwt
    public static Claims parseJwtToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(SECRET)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            throw new RuntimeException("Token解析错误");
        }
    }
}
