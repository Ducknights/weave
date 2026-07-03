package com.weave.chat.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

public class JwtUtil {

    private static final String SIGNING_KEY = "TianYongCheng";

    public static String getJwtSubject(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(SIGNING_KEY)
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }
}
