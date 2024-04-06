package com.preorder.service.facade;

import com.preorder.domain.*;
import com.preorder.dto.domaindto.OptionDto;
import com.preorder.dto.domaindto.OrderDto;
import com.preorder.dto.domaindto.ProductDto;
import com.preorder.dto.mapper.OptionMapper;
import com.preorder.dto.mapper.OrderMapper;
import com.preorder.dto.mapper.ProductMapper;
import com.preorder.dto.viewdto.OptionViewDto;
import com.preorder.dto.viewdto.OrderViewDto;
import com.preorder.dto.viewdto.ProductViewDto;
import com.preorder.global.error.dto.ErrorCode;
import com.preorder.global.error.exception.BusinessLogicException;
import com.preorder.infra.discord.DiscordBot;
import com.preorder.infra.noti.common.INotiService;
import com.preorder.infra.noti.common.MessageType;
import com.preorder.service.order.OrderService;
import com.preorder.service.product.OptionService;
import com.preorder.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderFacadeService {

    private final OrderService orderService;


    private final OrderMapper orderMapper;

    private final OptionMapper optionMapper;


    private final ProductMapper productMapper;

    private final OptionService optionService;

    private final ProductService productService;

    private final INotiService iNotiService;

    private final DiscordBot discordBot;

    @Transactional
    public void registerOrder(OrderViewDto orderViewDto) {

        assert (orderViewDto != null);

        //시간 요구조건 확인
        assert (orderViewDto.getReservationDate() != null);

        if (!orderService.checkReservationDate(orderViewDto.getReservationDate())) {
            log.error("reservation Date validation false");
            throw new BusinessLogicException(ErrorCode.BUSINESS_LOGIC_EXCEPTION_REGISTER_ORDER); // TO-DO 오류처리 및 로그
        }


        //주문기본 내용 저장
        OrderDto orderDto = orderMapper.changeTOoOrderDomainDto(orderViewDto);
        Order savedOrder = orderService.registerOrder(orderDto);

        //주문 상품정보 저장
        for (ProductViewDto productViewDto : orderViewDto.getProducts()) {

            ProductDto productDto = productMapper.changeToProductDomainDto(productViewDto);

            assert (productDto != null);
            Product product = productService.isExistProduct(productDto.getId());


            OrderProduct orderProduct = orderService.registerOrderProduct(savedOrder, product);

            //주문 옵션 저장
            for (OptionViewDto optionViewDto : productViewDto.getOptions()) {
                OptionDto optionDto = optionMapper.changeToOptionDomainDto(optionViewDto);

                assert (optionDto.getId() != null);
                assert (optionDto.getId() > 0);

                Option option = optionService.isExistOption(optionDto.getId());

                assert (option.getProduct() != null);
                boolean isMatch = optionService.matchProductAndOption(option, product.getId());
                if (!isMatch) {
                    log.error("option and product does not match option registered Fail");
                    continue;
                }
                optionService.saveOptionDetail(orderProduct, option, optionViewDto.getOptionValue());

            }
        }

        //구매자 주문성공 안내 메세지 발송
        //4 주문 성공 안내 메세지 발송
        String sendMessage = iNotiService.sendMessage(orderViewDto, MessageType.ORDER_CONFIRM);

        //관리자 Noti 발성
        //5 관리자에게 Discord 메세지로 주문안내
        discordBot.sendMessage("일반", sendMessage);


    }

    @Transactional(readOnly = true)
    public OrderViewDto getOrderDetail(OrderViewDto orderViewDto) {

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
}
