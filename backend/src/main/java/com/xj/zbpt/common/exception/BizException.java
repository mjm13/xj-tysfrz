package com.xj.zbpt.common.exception;

import com.xj.zbpt.common.response.ErrorCode;

/**
 * 业务异常：由业务代码主动抛出，由全局处理器转为统一失败响应。
 */
public class BizException extends RuntimeException {

    private final int code;

    public BizException(String message) {
        super(message);
        this.code = ErrorCode.BIZ_ERROR.code();
    }

    public BizException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BizException(ErrorCode errorCode) {
        super(errorCode.message());
        this.code = errorCode.code();
    }

    public int getCode() {
        return code;
    }
}
