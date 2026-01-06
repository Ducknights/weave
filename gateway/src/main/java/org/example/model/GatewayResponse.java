package org.example.model;


public class GatewayResponse<T> extends ApiResponse<T> {
    public GatewayResponse(int code, String msg, T data) {
        super(code, msg, data);
    }

    public static <T> GatewayResponse<T> of(GatewayStatus status, T data) {
        return new GatewayResponse<>(status.getCode(), status.getMsg(), data);
    }

    public static <T> GatewayResponse<T> error(T data) {
        return of(GatewayStatus.UNAUTHORIZED, data);
    }
}
