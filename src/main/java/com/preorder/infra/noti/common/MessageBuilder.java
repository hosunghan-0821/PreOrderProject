package com.preorder.infra.noti.common;


import com.preorder.dto.viewdto.OrderViewDto;
import com.preorder.dto.viewdto.ProductViewDto;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class MessageBuilder {

    private static final String MESSAGE_HEADER_PREFIX = "[파리바게트 다이아몬드 광장점]";

    private static final String PRE_PICKING_NOTICE_HEADER= "[사전예약 내역]";
    private static final String ORDER_CUSTOMER_PREFIX = "주문자 성함";
    private static final String ORDER_PHONE_NUM_PREFIX = "주문자 휴대번호";
    private static final String ORDER_RESERVATION_DAY_PREFIX= "픽업일시";
    private static final String ORDER_PRODUCT_PREFIX = "[주문 상품]";
    private static final String CHANGE_LINE = "\n";

    public static final ZoneId ASIA_SEOUL = ZoneId.of("Asia/Seoul");

    public String makeMessageConfirm(OrderViewDto orderViewDto) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(MESSAGE_HEADER_PREFIX).append(CHANGE_LINE);
        stringBuilder.append(CHANGE_LINE);
        stringBuilder.append(PRE_PICKING_NOTICE_HEADER).append(CHANGE_LINE);
        stringBuilder.append(ORDER_CUSTOMER_PREFIX + ": ").append(orderViewDto.getClientName()).append(CHANGE_LINE);
        stringBuilder.append(ORDER_PHONE_NUM_PREFIX + ": ").append(makeClientPhoneNumMask(orderViewDto.getClientPhoneNum())).append(CHANGE_LINE);
        stringBuilder.append(ORDER_RESERVATION_DAY_PREFIX+": ").append(makeReservationDate(orderViewDto.getReservationDate())).append(CHANGE_LINE);
        stringBuilder.append(CHANGE_LINE);
        stringBuilder.append(ORDER_PRODUCT_PREFIX).append(CHANGE_LINE);
        stringBuilder.append(makeProductInfoWithProductInfoList(orderViewDto.getProducts()));
        stringBuilder.append(CHANGE_LINE);
        stringBuilder.append(makeMessagePostFix());

        return stringBuilder.toString();
    }

    private String makeClientPhoneNumMask(String clientPhoneNum) {
        String maskedPhoneNum= clientPhoneNum.substring(0, clientPhoneNum.length() - 4);
        maskedPhoneNum=maskedPhoneNum+"****";
        return maskedPhoneNum;
    }

    private String makeReservationDate(Instant reservationDate){

        ZonedDateTime zonedDateTime = reservationDate.atZone(ASIA_SEOUL);
        return zonedDateTime.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시"));

    }

    private String makeProductInfoWithProductInfoList(List<ProductViewDto> productViewDtoList){
        Map<String,Integer> productMap = new HashMap<>();
        StringBuilder stringBuilder = new StringBuilder();
        for(ProductViewDto productViewDto: productViewDtoList){
            productMap.merge(productViewDto.getName(), 1, Integer::sum);
        }
        productMap.entrySet().forEach(entry->{
            stringBuilder.append(entry.getKey()).append(": ").append(entry.getValue()).append("개").append("\n");
        });

        return stringBuilder.toString();
    }
    private String makeMessagePostFix() {
        return "문의사항은 매장에 전화주세요 \n다이아몬드 광장점: 031-405-1617";
    }
}
