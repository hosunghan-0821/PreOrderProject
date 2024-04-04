package com.preorder.global.error.exception;

import com.preorder.global.error.dto.ErrorCode;

public class BusinessLogicException  extends RuntimeException{

    private ErrorCode errorCode;

    public BusinessLogicException() {
        this.errorCode = ErrorCode.BUSINESS_LOGIC_EXCEPTION;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public BusinessLogicException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}
