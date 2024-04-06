package com.preorder.infra.noti.sms;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.preorder.dto.viewdto.OrderViewDto;
import com.preorder.global.config.ApplicationOptionConfig;
import com.preorder.global.error.dto.ErrorCode;
import com.preorder.global.error.exception.InternalServerException;
import com.preorder.global.error.exception.InvalidArgumentException;
import com.preorder.infra.noti.common.INotiService;
import com.preorder.infra.noti.common.MessageBuilder;
import com.preorder.infra.noti.common.MessageType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class SMSService implements INotiService {
    @Value("${naver-cloud-sms.accessKey}")
    private String accessKey;

    @Value("${naver-cloud-sms.secretKey}")
    private String secretKey;

    @Value("${naver-cloud-sms.serviceId}")
    private String serviceId;

    @Value("${naver-cloud-sms.senderPhone}")
    private String phone;

    private final ObjectMapper objectMapper;

    private final MessageBuilder messageBuilder;

    private final ApplicationOptionConfig applicationOptionConfig;

    public void sendMessage(List<NotiMessageDto> NotiMessageDtoList) {

        if(!applicationOptionConfig.isSMSService()){
            log.info("SMS Service off");
            return;
        }
        final RestTemplate rt = new RestTemplate();
        final HttpHeaders headers = makeSMSRequestHeader();
        final SMSRequestDto smsRequestDto = makeSMSRequestDto(NotiMessageDtoList);

        try {
            String body = objectMapper.writeValueAsString(smsRequestDto);
            HttpEntity<String> request = new HttpEntity<>(body, headers);

            final ResponseEntity<SMSResponseDto> response = rt.exchange("https://sens.apigw.ntruss.com/sms/v2/services/" + serviceId + "/messages", HttpMethod.POST, request, SMSResponseDto.class);
            //성공 할 때도 문자 발송이 되는 것이기 때문에 개인적으로 Notice해줄 수 있어야 함.
            log.info("SMS 발신 성공");

        } catch (Exception e) {

            throw new InternalServerException(ErrorCode.INTERNAL_SERVER_ERROR);
        }


    }

    public <T> NotiMessageDto makeSMSMessage(T data, MessageType messageType) {

        switch (messageType) {
            case ORDER_CONFIRM:
                if (data instanceof OrderViewDto) {
                    OrderViewDto orderViewDto = (OrderViewDto) data;
                    NotiMessageDto notiMessageDto = new NotiMessageDto();
                    notiMessageDto.setTo(orderViewDto.getClientPhoneNum().replaceAll("-", ""));
                    notiMessageDto.setContent(messageBuilder.makeMessageConfirm(orderViewDto));
                    log.info(notiMessageDto.getContent());
                    return notiMessageDto;
                }

                break;
            case ORDER_CANCEL:
                /*
                 * TO-DO
                 * 주문 취소 문자 메시지 형식 만들어야함
                 * */
                break;
            default:
               break;
        }
        throw new InvalidArgumentException(ErrorCode.INVALID_ARGUMENT_EXCEPTION);
    }

    private SMSRequestDto makeSMSRequestDto(List<NotiMessageDto> notiMessageDtoList) {


        return SMSRequestDto.builder()
                .type("LMS")
                .contentType("COMM")
                .countryCode("82")
                .from(phone.replace("\"",""))
                .content("[파리바게트 다이아몬드 광장점]")
                .messages(notiMessageDtoList)
                .build();

    }

    private HttpHeaders makeSMSRequestHeader() {

        Long time = System.currentTimeMillis();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-ncp-apigw-timestamp", time.toString());
        headers.set("x-ncp-iam-access-key", accessKey);
        headers.set("x-ncp-apigw-signature-v2", makeSMSRequestHeaderSignature(time));

        return headers;
    }

    private String makeSMSRequestHeaderSignature(Long time) {

        String space = " ";
        String newLine = "\n";
        String method = "POST";
        String url = "/sms/v2/services/" + this.serviceId + "/messages";
        String timestamp = time.toString();
        String accessKey = this.accessKey;
        String secretKey = this.secretKey;

        String message = new StringBuilder()
                .append(method)
                .append(space)
                .append(url)
                .append(newLine)
                .append(timestamp)
                .append(newLine)
                .append(accessKey)
                .toString();

        try {
            SecretKeySpec signingKey = new SecretKeySpec(secretKey.getBytes("UTF-8"), "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(signingKey);

            byte[] rawHmac = mac.doFinal(message.getBytes("UTF-8"));
            String encodeBase64String = Base64.encodeBase64String(rawHmac);

            return encodeBase64String;
        } catch (Exception e) {

            throw new InternalServerException();
        }

    }

    @Override
    @Async
    public String sendMessage(OrderViewDto orderViewDto, MessageType messageType) {
        final NotiMessageDto notiMessageDto = makeSMSMessage(orderViewDto, MessageType.ORDER_CONFIRM);
        sendMessage(Arrays.asList(notiMessageDto));

        return notiMessageDto.getContent().toString();
    }


}
