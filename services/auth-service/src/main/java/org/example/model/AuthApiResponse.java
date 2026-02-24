package org.example.model;


import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AuthApiResponse<T> extends ApiResponse<T>{

    public AuthApiResponse(int code, String msg, T data) {
        super(code, msg, data);
    }

    public static <T> AuthApiResponse<T> of(AuthApiStatus status, T data) {
        return new AuthApiResponse<>(status.getCode(), status.getMsg(), data);
    }
}
