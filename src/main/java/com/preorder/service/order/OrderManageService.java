package com.preorder.service.order;


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
import com.preorder.global.cache.CacheService;
import com.preorder.global.error.dto.ErrorCode;
import com.preorder.global.error.exception.BusinessLogicException;
import com.preorder.service.product.OptionService;
import com.preorder.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderManageService {

    private final OrderMapper orderMapper;

    private final ProductMapper productMapper;

    private final OptionMapper optionMapper;

    private final OrderService orderService;

    private final ProductService productService;

    private final OptionService optionService;

    private final CacheService cacheService;

    @Transactional
    public void registerOrderWithLock(OrderViewDto orderViewDto, Map<Long, Long> orderMapInfo) {

        List<Long> orderProductIdList = new ArrayList<>(orderMapInfo.keySet());


        // ReidsCache singleThread atomic 보장 needNot Locking

        //1. redis에 cache 존재여부 확인


        //존재하면 redisCache로직 태우고
        boolean result = cacheService.decreaseProductNumByOrder(orderMapInfo);
        if(!result) {
            throw new BusinessLogicException(ErrorCode.BUSINESS_LOGIC_EXCEPTION_REMAIN_PRODUCT_ERROR);
        }

        //없으면 기존 JPA 비관락 세팅..
        List<Product> productList = productService.getProductByIds(orderProductIdList);


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
    }
}
