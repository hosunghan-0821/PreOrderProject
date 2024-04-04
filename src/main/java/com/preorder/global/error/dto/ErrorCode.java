package com.preorder.global.error.dto;

public enum ErrorCode {

    NOT_FOUND_EXCEPTION("요청한 리소스를 찾지 못하였습니다.", "404000"),
    NOT_FOUND_EXCEPTION_PRODUCT("요청한 상품을 찾지 못하였습니다.", "404002"),
    NOT_FOUND_EXCEPTION_OPTION("요청한 옵션을 찾지 못하였습니다.", "404003"),

    NOT_FOUND_EXCEPTION_PRODUCT_OPTION("요청한 상품에 맞는 옵션을 찾지 못하였습니다","404004"),
    INTERNAL_SERVER_ERROR("서버 내부 오류", "500000"),
    INTERNAL_SERVER_ERROR_PARSING_ERROR("서버 내부 파싱 오류", "500001"),
    INVALID_ARGUMENT_EXCEPTION("요청하신 데이터에 문제가 있습니다.", "400000"),

    INVALID_METHOD_ARGUMENT("요청하신 데이터의 유효성 검사에 실패하였습니다.", "400001");
    private String defaultMessage;
    private String errorCode;

    ErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    ErrorCode(String defaultMessage, String errorCode) {
        this.defaultMessage = defaultMessage;
        this.errorCode = errorCode;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
