package com.xj.zbpt.common.response;

/**
 * 统一响应体：所有 REST 接口返回此结构。
 *
 * @param code    0 表示成功，非 0 表示失败
 * @param message 描述信息
 * @param data    业务数据，失败时为 null
 */
public record ApiResponse<T>(int code, String message, T data) {

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(ErrorCode.SUCCESS.code(), ErrorCode.SUCCESS.message(), data);
    }

    public static <T> ApiResponse<T> fail(int code, String message) {
        return new ApiResponse<>(code, message, null);
    }

    public static <T> ApiResponse<T> fail(ErrorCode errorCode) {
        return new ApiResponse<>(errorCode.code(), errorCode.message(), null);
    }
}
