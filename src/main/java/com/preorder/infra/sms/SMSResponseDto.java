package com.preorder.infra.sms;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter(AccessLevel.PROTECTED)
public class SMSResponseDto {

    private String requestId;
    private String requestTime;
    private String statusCode;
    private String statusName;

}
