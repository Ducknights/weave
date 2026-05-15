package org.example.util;

import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

@Component
public class JwtUtil {

    //生成token
    public String generateJwtToken(String subject,int expiration) {
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
                .signWith(io.jsonwebtoken.SignatureAlgorithm.HS256,"TianYongCheng")
                .compact();
    }
}
