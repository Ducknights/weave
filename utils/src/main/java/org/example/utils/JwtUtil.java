package org.example.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

@Component
public class JwtUtil {

    private static final String SECRET = "ChenXin";

    //生成token
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

    //验证token
    public static String getJwtSubject(String token) {
        // 解析Token得到用户标识信息
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(SECRET)
                    .parseClaimsJws(token)
                    .getBody();

            return claims.getSubject();
        } catch (Exception e) {
            throw new RuntimeException("Token解析错误");
        }
    }

    public static void main(String[] args) {
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJqdGkiOiI2YTA3NzMzNi0yZjg0LTRhZjUtYWZkMC1jNWNjNTg0MjViNTEiLCJzdWIiOiJVc2VySWQ6MSIsImlzcyI6IlRpYW5Zb25nQ2hlbmciLCJpYXQiOjE3NTU2NzkxNjAsImV4cCI6MTc1NTc2NTU2MH0.tApIb4zAc5o3H3GHwtBVE7HGFC463ExwNIEw9K2e21s")
                .getBody();
        String subject = claims.getSubject();
        System.out.println(subject.substring(7));
    }
}
