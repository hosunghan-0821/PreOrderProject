package com.preorder.service.facade;

import com.preorder.domain.OptionDetail;
import com.preorder.domain.OrderProduct;
import com.preorder.domain.Product;
import com.preorder.dto.domaindto.OrderDto;
import com.preorder.dto.mapper.OptionMapper;
import com.preorder.dto.mapper.OrderMapper;
import com.preorder.dto.mapper.ProductMapper;
import com.preorder.dto.viewdto.OptionViewDto;
import com.preorder.dto.viewdto.OrderViewDto;
import com.preorder.dto.viewdto.ProductViewDto;
import com.preorder.global.cache.CacheString;
import com.preorder.global.error.dto.ErrorCode;
import com.preorder.global.error.exception.BusinessLogicException;
import com.preorder.infra.discord.DiscordBot;
import com.preorder.infra.noti.common.INotiService;
import com.preorder.infra.noti.common.MessageType;
import com.preorder.service.order.OrderManageService;
import com.preorder.service.order.OrderService;
import com.preorder.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderFacadeService {

    private final OrderService orderService;


    private final OrderMapper orderMapper;

    private final OptionMapper optionMapper;


    private final ProductMapper productMapper;

    private final ProductService productService;

    private final INotiService iNotiService;

    private final DiscordBot discordBot;

    private final OrderManageService orderManageService;

    public void registerOrder(OrderViewDto orderViewDto) {


        assert (orderViewDto != null);
        assert (orderViewDto.getReservationDate() != null);

        if (!orderService.checkReservationDate(orderViewDto.getReservationDate())) {

            throw new BusinessLogicException(ErrorCode.BUSINESS_LOGIC_EXCEPTION_REGISTER_ORDER);
        }

        //재고확인 1차 Early Return을 위해서 락없이
        Map<Long, Long> orderInfoMap = getOrderInfoMap(orderViewDto.getProducts());
        if (!checkProductNumIsValid(orderInfoMap)) {
            throw new BusinessLogicException(ErrorCode.BUSINESS_LOGIC_EXCEPTION_REMAIN_PRODUCT_ERROR);
        }

        //DB락 얻은 상태에서 제고 개수 다시 비교 및 처리
        orderManageService.registerOrderWithLock(orderViewDto,orderInfoMap);

        //After Commit EventListner로 처리..

        //구매자 주문성공 안내 메세지 발송
        //4 주문 성공 안내 메세지 발송
        String sendMessage = iNotiService.sendMessage(orderViewDto, MessageType.ORDER_CONFIRM);

        //관리자 Noti 발성
        //5 관리자에게 Discord 메세지로 주문안내
        discordBot.sendMessage("일반", sendMessage);


    }


    @Transactional(readOnly = true)
    @Cacheable(value = CacheString.ORDER_DETAIL_CACHE, key = "#orderViewDto.getId")
    public OrderViewDto getOrderDetail(OrderViewDto orderViewDto) {

        //조회규칙 주문 ID,  핸드폰 번호,성명 모두 같아야 조회
        assert (orderViewDto.getId() != null);
        assert (orderViewDto.getClientName() != null && !orderViewDto.getClientName().isEmpty());
        assert (orderViewDto.getClientPhoneNum() != null);

        //기본주문정보
        final OrderDto orderDomainDto = orderMapper.changeTOoOrderDomainDto(orderViewDto);
        List<OrderProduct> orderProductList = orderService.getOrder(orderDomainDto);

        assert (!orderProductList.isEmpty());

        orderViewDto = orderMapper.toOrderViewDto(orderProductList.get(0).getOrder());

        List<ProductViewDto> productViewDtoList = new ArrayList<>();

        for (OrderProduct orderProduct : orderProductList) {
            Product product = orderProduct.getProduct();
            ProductViewDto productViewDto = productMapper.changeToProductViewDto(product);


            List<OptionViewDto> optionViewDtoList = new ArrayList<>();

            for (OptionDetail optionDetail : orderProduct.getOptionDetails()) {
                OptionViewDto optionViewDto = optionMapper.changeToOptionViewDto(optionDetail.getOption());
                optionViewDto.setDetailValue(optionDetail.getId(), optionDetail.getOptionValue());
                optionViewDtoList.add(optionViewDto);
            }
            productViewDto.setOptionViewDtoList(optionViewDtoList);
            productViewDtoList.add(productViewDto);
        }

        orderViewDto.setProductViewDtoList(productViewDtoList);

        return orderViewDto;
    }

    public Page<OrderViewDto> getOrder(Pageable pageable) {

        assert (pageable != null);
        Page<OrderDto> orderList = orderService.getOrderList(pageable);
        List<OrderViewDto> orderViewDtoList = orderList.stream().map(orderMapper::toOrderViewDto).collect(Collectors.toList());

        return new PageImpl<>(orderViewDtoList, pageable, orderList.getTotalElements());
    }


    private boolean checkProductNumIsValid(Map<Long, Long> productMap) {

        assert (productMap != null);
        assert (!productMap.isEmpty());


        List<Long> collect = new ArrayList<>(productMap.keySet());
        List<Product> productInfoList = productService.getProductByIds(collect);


        for (var product : productInfoList) {
            Long orderProductNum = productMap.get(product.getId());
            if (orderProductNum > product.getProductNum()) {
                return false;
            }
        }

        return true;
    }



    public Map<Long, Long> getOrderInfoMap(List<ProductViewDto> products) {
        Map<Long, Long> productMap = new HashMap<>();
        //상품 개수 정보 추출
        for (var productViewDto : products) {
            Long id = productViewDto.getId();
            if (!productMap.containsKey(id)) {
                productMap.put(id, 1L);
            } else {
                productMap.put(id, productMap.get(id) + 1);
            }
        }
        return productMap;
    }

}
