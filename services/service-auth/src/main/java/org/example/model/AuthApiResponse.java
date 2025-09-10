package org.example.model;


import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AuthApiResponse<T> extends ApiResponse<T>{

    public AuthApiResponse(int code, String msg, T data) {
        super(code, msg, data);
    }

    public static <T> AuthApiResponse<T> of(ApiStatus status, T data) {
        return new AuthApiResponse<>(status.getCode(), status.getMsg(), data);
    }

    public static <T> AuthApiResponse<T> loginSuccess(T data) {
        return of(ApiStatus.LOGIN_SUCCESS, data);
    }
    public static <T> AuthApiResponse<T> loginFail(T msg) {
        return of(ApiStatus.LOGIN_FAILED, msg);
    }
    public static <T> AuthApiResponse<T> registerSuccess() {
        return of(ApiStatus.REGISTER_SUCCESS,null);
    }
    public static <T> AuthApiResponse<T> registerFail(T msg) {
        return of(ApiStatus.REGISTER_FAILED, msg);
    }
    public static <T> AuthApiResponse<T> logOutSuccess() {
        return of(ApiStatus.LOGIN_SUCCESS,null);
    }
    public static <T> AuthApiResponse<T> getNewTokenSuccess(T data) {
        return of(ApiStatus.TOKEN_SUCCESS, data);
    }
    public static <T> AuthApiResponse<T> getNewTokenFail(T msg) {
        return of(ApiStatus.TOKEN_FAILED, msg);
    }
}
