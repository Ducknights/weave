package org.example;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import java.util.Date;
import java.util.UUID;


public class JwtUtil {

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

    //解析jwt
    public static Claims parseJwtToken(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET)
                .parseClaimsJws(token)
                .getBody();
    }

    public static void main(String[] args) {
        Claims claims = parseJwtToken("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxZDhlNTRkNS1mNjE2LTQxMTAtYWE3NC1mYjNiZGZhYWNhNGEiLCJzdWIiOiJVc2VySWQ6MTAiLCJpc3MiOiJUaWFuWW9uZ0NoZW5nIiwiaWF0IjoxNzU0MzI3MDgzLCJleHAiOjE3NTQzMjczODN9.SBHm3tUna-SqHc_fC-QjRPkW3Fn_wgtZiNfMs_yY60I");
        System.out.println(claims);
    }
}
