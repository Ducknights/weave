package org.example.model;

public class ClubApiResponse<T> extends ApiResponse<T>{
    public ClubApiResponse(int code, String msg, T data) {
        super(code, msg, data);
    }
    public static <T> ClubApiResponse<T> of(ClubApiStatus status, T data) {
        return new ClubApiResponse<>(status.getCode(), status.getMsg(), data);
    }
    public static <T> ClubApiResponse<T> success(ClubApiStatus status,T data) {
        return of(status, data);
    }
    public static <T> ClubApiResponse<T> fail(ClubApiStatus status,T data) {
        return of(status, data);
    }
}
