package com.preorder.infra.noti.sms;


import lombok.*;

import java.util.List;


@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter(AccessLevel.PROTECTED)
public class SMSRequestDto {

    private String type;
    private String contentType;
    private String countryCode;
    private String from;
    private String content;
    private List<NotiMessageDto> messages;
}
