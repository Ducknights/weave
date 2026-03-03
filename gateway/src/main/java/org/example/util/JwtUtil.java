package org.example.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

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
}
