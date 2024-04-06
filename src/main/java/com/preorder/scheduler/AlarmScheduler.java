package com.preorder.scheduler;


import com.preorder.domain.Order;
import com.preorder.domain.OrderProduct;
import com.preorder.domain.Product;
import com.preorder.dto.mapper.OrderMapper;
import com.preorder.dto.mapper.ProductMapper;
import com.preorder.dto.viewdto.OrderViewDto;
import com.preorder.dto.viewdto.ProductViewDto;
import com.preorder.infra.discord.DiscordBot;
import com.preorder.infra.noti.common.MessageBuilder;
import com.preorder.service.order.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class AlarmScheduler {

    private final OrderService orderService;

    private final DiscordBot discordBot;

    private final OrderMapper orderMapper;

    private final ProductMapper productMapper;

    private final MessageBuilder messageBuilder;


    @Scheduled(cron = "0 0 18 * * *") // 매일 오후 6시에 실행
    public void alarmToManager() {

        List<OrderProduct> orderProductList = orderService.getProductToOrder();

        assert(orderProductList != null);

        HashMap<Long, List<Product>> productMap = new HashMap<>();
        HashMap<Long, Order> orderMap = new HashMap<>();

        for (OrderProduct orderProduct : orderProductList) {
            Long orderId = orderProduct.getOrder().getId();
            if (orderMap.containsKey(orderId)) {
                List<Product> productList = productMap.get(orderId);
                productList.add(orderProduct.getProduct());
            } else {
                orderMap.put(orderId, orderProduct.getOrder());
                productMap.put(orderId, new ArrayList<>(Arrays.asList(orderProduct.getProduct())));
            }
        }

        for (Map.Entry<Long, List<Product>> entry : productMap.entrySet()) {
            OrderViewDto orderViewDto = orderMapper.toOrderViewDto(orderMap.get(entry.getKey()));

            List<ProductViewDto> productViewDtoList = entry.getValue().stream().map(productMapper::changeToProductViewDto).collect(Collectors.toList());
            orderViewDto.setProductViewDtoList(productViewDtoList);

            String message = messageBuilder.makeMessageConfirm(orderViewDto);
            discordBot.sendMessage("주문조회", message);
        }

    }

    //SMS 일단 보류 TO-DO
    public void alarmToClient() {

    }


}
