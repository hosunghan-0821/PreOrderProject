package com.preorder.infra.noti;

import com.preorder.dto.viewdto.OrderViewDto;
import com.preorder.infra.sms.MessageType;

public interface INotiService {

    String sendMessage(OrderViewDto orderViewDto, MessageType messageType);

}
