package org.example.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.ResponseEntity;

@Data
@EqualsAndHashCode(callSuper = true)
public class ResourcesApiResponse<T> extends ApiResponse<T>{
    public ResourcesApiResponse(int code, String msg, T data) {
        super(code, msg, data);
    }
    public static <T> ResourcesApiResponse<T> of(ApiStatus status, T data) {
        return new ResourcesApiResponse<>(status.getCode(), status.getMsg(), data);
    }

    public static <T> ResourcesApiResponse<T> getSuccess(T data) {
        return of(ApiStatus.GET_SUCCESS, data);
    }

    public static <T> ResourcesApiResponse<T> getFail(T msg) {
        return of(ApiStatus.GET_FAIL, msg);
    }

    public static <T> ResourcesApiResponse<T> postSuccess(T data) {
        return of(ApiStatus.POST_SUCCESS, data);
    }

    public static <T> ResourcesApiResponse<T> postFail(T msg) {
        return of(ApiStatus.POST_FAIL, msg);
    }

    public static <T> ResourcesApiResponse<T> putSuccess(T data) {
        return of(ApiStatus.PUT_SUCCESS, data);
    }

    public static <T> ResourcesApiResponse<T> putFail(T msg) {
        return of(ApiStatus.PUT_FAIL, msg);
    }

    public static <T> ResourcesApiResponse<T> deleteSuccess(T data) {
        return of(ApiStatus.DELETE_SUCCESS, data);
    }

    public static <T> ResourcesApiResponse<T> deleteFail(T msg) {
        return of(ApiStatus.DELETE_FAIL, msg);
    }
    public static <T> ResourcesApiResponse<T> uploadSuccess(T data) {
        return of(ApiStatus.POST_SUCCESS, data);
    }
    public static <T> ResourcesApiResponse<T> uploadFail(T msg) {
        return of(ApiStatus.POST_FAIL, msg);
    }
    public static <T> ResourcesApiResponse<T> downloadSuccess(T data) {
        return of(ApiStatus.GET_SUCCESS, data);
    }
    public static <T> ResourcesApiResponse<T> downloadFail(T msg) {
        return of(ApiStatus.GET_FAIL, msg);
    }
    public static <T> ResourcesApiResponse<T> error(T data) {
        return of(ApiStatus.DELETE_SUCCESS, data);
    }
}
