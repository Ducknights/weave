package com.weave.auth.model.dto;

public record TokenDto(
        String token_type,
        String access_token,
        Integer access_token_expires_in,
        String refresh_token,
        Integer refresh_token_expires_in
) {

    public TokenDto(String access, Integer accessExpire, String refresh , Integer refreshExpire) {
        this("Bearer", access, accessExpire, refresh, refreshExpire);
    }
}
