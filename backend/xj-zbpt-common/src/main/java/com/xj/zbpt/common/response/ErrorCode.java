package com.xj.zbpt.common.response;

/**
 * 统一错误码。0 表示成功，其余为各类失败。
 * 业务 change 可在各自模块定义更细的错误码，避免污染通用层。
 */
public enum ErrorCode {

    SUCCESS(0, "success"),
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未认证"),
    FORBIDDEN(403, "无访问权限"),
    BIZ_ERROR(1000, "业务处理失败"),
    INTERNAL_ERROR(500, "服务器内部错误");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int code() {
        return code;
    }

    public String message() {
        return message;
    }
}
