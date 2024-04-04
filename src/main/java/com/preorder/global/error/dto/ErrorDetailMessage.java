package com.preorder.global.error.dto;

public class ErrorDetailMessage {

    public static final String NULL ="널 값이어야 합니다.";
    public static final String NOT_NULL="널을 허용하지 않습니다.";

    public static final String NOT_NULL_CLIENT_NAME ="주문자 성함을 입력하세요";

    public static final String NOT_NULL_CLIENT_PHONE_NUM = "주문자 번호를 입력하세요";

    public static final String  NOT_NULL_RESERVATION_DATE = "주문 픽업날짜를 입력하세요";

    //Instance 생성 방지
    private ErrorDetailMessage(){

    }
}
