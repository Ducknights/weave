package org.example.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T>{

    private int code;
    private String msg;
    private T data;

    public static <T> ApiResponse<T> of(ApiStatus status, T data) {
        return new ApiResponse<>(status.getCode(), status.getMsg(),data);
    }

    public static <T> ApiResponse<T> loginSuccess(T data) {
        return of(ApiStatus.LOGIN_SUCCESS, data);
    }
    public static <T> ApiResponse<T> loginFail(T msg) {
        return of(ApiStatus.LOGIN_FAILED, msg);
    }
    public static <T> ApiResponse<T> registerSuccess() {
        return of(ApiStatus.REGISTER_SUCCESS,null);
    }
    public static <T> ApiResponse<T> registerFail(T msg) {
        return of(ApiStatus.REGISTER_FAILED, msg);
    }
    public static <T> ApiResponse<T> logOutSuccess() {
        return of(ApiStatus.LOGIN_SUCCESS,null);
    }
}
