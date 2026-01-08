package org.example.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ResourcesApiResponse<T> extends ApiResponse<T>{
    public ResourcesApiResponse(int code, String msg, T data) {
        super(code, msg, data);
    }
    public static <T> ResourcesApiResponse<T> of(ResourceApiStatus status, T data) {
        return new ResourcesApiResponse<>(status.getCode(), status.getMsg(), data);
    }

    public static <T> ResourcesApiResponse<T> getSuccess(T data) {
        return of(ResourceApiStatus.GET_SUCCESS, data);
    }

    public static <T> ResourcesApiResponse<T> getFail(T msg) {
        return of(ResourceApiStatus.GET_FAIL, msg);
    }

    public static <T> ResourcesApiResponse<T> postSuccess(T data) {
        return of(ResourceApiStatus.POST_SUCCESS, data);
    }

    public static <T> ResourcesApiResponse<T> postFail(T msg) {
        return of(ResourceApiStatus.POST_FAIL, msg);
    }

    public static <T> ResourcesApiResponse<T> putSuccess(T data) {
        return of(ResourceApiStatus.PUT_SUCCESS, data);
    }

    public static <T> ResourcesApiResponse<T> putFail(T msg) {
        return of(ResourceApiStatus.PUT_FAIL, msg);
    }

    public static <T> ResourcesApiResponse<T> deleteSuccess(T data) {
        return of(ResourceApiStatus.DELETE_SUCCESS, data);
    }

    public static <T> ResourcesApiResponse<T> deleteFail(T msg) {
        return of(ResourceApiStatus.DELETE_FAIL, msg);
    }
    public static <T> ResourcesApiResponse<T> uploadSuccess(T data) {
        return of(ResourceApiStatus.POST_SUCCESS, data);
    }
    public static <T> ResourcesApiResponse<T> uploadFail(T msg) {
        return of(ResourceApiStatus.POST_FAIL, msg);
    }
    public static <T> ResourcesApiResponse<T> downloadSuccess(T data) {
        return of(ResourceApiStatus.GET_SUCCESS, data);
    }
    public static <T> ResourcesApiResponse<T> downloadFail(T msg) {
        return of(ResourceApiStatus.GET_FAIL, msg);
    }
    public static <T> ResourcesApiResponse<T> error(ResourceApiStatus status,T data) {
        return of(status, data);
    }
}
