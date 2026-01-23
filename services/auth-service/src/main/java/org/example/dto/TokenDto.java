package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokenDto {
    private String token_type = "Bearer";
    private String access_token;
    private int access_token_expires_in;
    private String refresh_token;
    private int refresh_token_expires_in;

    public TokenDto(String access, String refresh, int accessExpire, int refreshExpire) {
        this.access_token = access;
        this.refresh_token = refresh;
        this.access_token_expires_in = accessExpire;
        this.refresh_token_expires_in = refreshExpire;
    }
}
