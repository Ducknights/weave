package org.example.dto;

public record TokenDto(
        String token_type,
        String access_token,
        int access_token_expires_in,
        String refresh_token,
        int refresh_token_expires_in
) {

    public TokenDto(String access, int accessExpire, String refresh ,int refreshExpire) {
        this("Bearer", access, accessExpire, refresh, refreshExpire);
    }
}
