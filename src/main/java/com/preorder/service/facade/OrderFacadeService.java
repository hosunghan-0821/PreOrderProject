package com.preorder.service.facade;

import com.preorder.domain.Option;
import com.preorder.domain.Order;
import com.preorder.domain.OrderProduct;
import com.preorder.domain.Product;
import com.preorder.dto.domaindto.OptionDto;
import com.preorder.dto.domaindto.OrderDto;
import com.preorder.dto.domaindto.ProductDto;
import com.preorder.dto.mapper.OptionMapper;
import com.preorder.dto.mapper.OrderMapper;
import com.preorder.dto.mapper.ProductMapper;
import com.preorder.dto.viewdto.OptionViewDto;
import com.preorder.dto.viewdto.OrderViewDto;
import com.preorder.dto.viewdto.ProductViewDto;
import com.preorder.service.order.OrderService;
import com.preorder.service.product.OptionService;
import com.preorder.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderFacadeService {

    private final OrderService orderService;


    private final OrderMapper orderMapper;

    private final OptionMapper optionMapper;


    private final ProductMapper productMapper;

    private final OptionService optionService;

    private final ProductService productService;

    @Transactional
    public void registerOrder(OrderViewDto orderViewDto) {

        assert (orderViewDto != null);

        //시간 요구조건 확인

        //주문기본 내용 저장
        OrderDto orderDto = orderMapper.toOrderDomainDto(orderViewDto);
        Order savedOrder = orderService.registerOrder(orderDto);

        //주문 상품정보 저장
        for (ProductViewDto productViewDto: orderViewDto.getProducts()) {

            ProductDto productDto = productMapper.changeToProductDomainDto(productViewDto);

            assert (productDto != null);
            Product existProduct = productService.isExistProduct(productDto.getId());


            OrderProduct orderProduct = orderService.registerOrderProduct(savedOrder, existProduct);

            //주문 옵션 저장
            for (OptionViewDto optionViewDto : productViewDto.getOptions()) {
                OptionDto optionDto = optionMapper.changeToOptionDomainDto(optionViewDto);

                assert (optionDto.getId() != null);
                assert (optionDto.getId() > 0);

                Option option = optionService.isExistOption(optionDto.getId());

                optionService.saveOptionDetail(orderProduct, option, optionViewDto.getOptionValue());

            }
        }

        //구매자 주문성공 안내 메세지 발송

        //관리자 Noti 발성




    }
}
