package com.preorder.infra.sms;


import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter(AccessLevel.PROTECTED)
public class NotiMessageDto {
    private String to;
    private String content;
}
