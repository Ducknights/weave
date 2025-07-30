package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse <T>{
    private int code;
    private String msg;
    private String token;
    private T data;
    private long timestamp;

    public static <T> AuthResponse<T> authSuccess(String token, String msg,T data) {
        return new AuthResponse<T>(200, msg, token, data, System.currentTimeMillis());
    }

    public static <T> AuthResponse<T> authFail(Integer code, String msg) {
        return new AuthResponse<>(code,msg,null, null, System.currentTimeMillis());
    }

    public static <T> AuthResponse<T> signSuccess(String msg) {
        return new AuthResponse<>(200,msg,null, null, System.currentTimeMillis());
    }

    public static <T> AuthResponse<T> signFail(Integer code, String msg) {
        return new AuthResponse<>(code,msg,null, null, System.currentTimeMillis());
    }

    public static <T> AuthResponse<T> logoutSuccess(String msg) {
        return new AuthResponse<>(200, msg, null, null, System.currentTimeMillis());
    }
    public static <T> AuthResponse<T> logoutFail(Integer code, String msg) {
        return new AuthResponse<>(code, msg, null, null, System.currentTimeMillis());
    }
}
