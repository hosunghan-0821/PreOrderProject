package com.preorder.infra.noti.common;

import com.preorder.dto.viewdto.OrderViewDto;

public interface INotiService {

    String sendMessage(OrderViewDto orderViewDto, MessageType messageType);

}
