package com.weave.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import java.util.Date;
import java.util.UUID;

public class JwtUtil {

    //验证token
    public static String getJwtSubject(String token) {
        // 解析Token得到用户标识信息
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey("TianYongCheng")
                    .parseClaimsJws(token)
                    .getBody();

            return claims.getSubject();
        } catch (Exception e) {
            throw new RuntimeException("Token解析错误");
        }
    }

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
                .signWith(io.jsonwebtoken.SignatureAlgorithm.HS256,"TianYongCheng")
                .compact();
    }
}
