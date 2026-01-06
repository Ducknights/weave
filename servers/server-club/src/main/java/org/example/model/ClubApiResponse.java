package org.example.model;

public class ClubApiResponse<T> extends ApiResponse<T>{
    public ClubApiResponse(int code, String msg, T data) {
        super(code, msg, data);
    }
    public static <T> ClubApiResponse<T> of(ClubApiStatus status, T data) {
        return new ClubApiResponse<>(status.getCode(), status.getMsg(), data);
    }
    public static <T> ClubApiResponse<T> getSuccess(T data) {
        return of(ClubApiStatus.GET_SUCCESS, data);
    }
    public static <T> ClubApiResponse<T> getFail(T msg) {
        return of(ClubApiStatus.GET_FAIL, msg);
    }
    public static <T> ClubApiResponse<T> postSuccess(T data) {
        return of(ClubApiStatus.POST_SUCCESS, data);
    }
    public static <T> ClubApiResponse<T> postFail(T msg) {
        return of(ClubApiStatus.POST_FAIL, msg);
    }
    public static <T> ClubApiResponse<T> putSuccess(T data) {
        return of(ClubApiStatus.PUT_SUCCESS, data);
    }
    public static <T> ClubApiResponse<T> putFail(T msg) {
        return of(ClubApiStatus.PUT_FAIL, msg);
    }
    public static <T> ClubApiResponse<T> deleteSuccess() {
        return of(ClubApiStatus.DELETE_SUCCESS,null);
    }
    public static <T> ClubApiResponse<T> deleteFail(T data) {
        return of(ClubApiStatus.DELETE_FAIL, data);
    }
    public static <T> ClubApiResponse<T> error(ClubApiStatus status,T data) {
        return of(status, data);
    }
}
