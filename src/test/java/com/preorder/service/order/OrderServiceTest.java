package com.preorder.service.order;

import com.preorder.domain.Order;
import com.preorder.domain.OrderProduct;
import com.preorder.domain.Product;
import com.preorder.dto.domaindto.OrderDto;
import com.preorder.dto.mapper.OrderMapper;
import com.preorder.repository.order.OrderProductRepository;
import com.preorder.repository.order.OrderRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;


@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private  OrderMapper orderMapper;
    @Mock
    private OrderProductRepository orderProductRepository;


    @Mock
    private OrderRepository orderRepository;


    @Test
    @DisplayName("[성공]주문 : 주문정보 등록 테스트")
    void registerOrderTest() {

        //given
        doReturn(Order.builder().id(1L).build())
                .when(orderRepository)
                .save(any(Order.class));

        doReturn(Order.builder().id(1L).build())
                .when(orderMapper)
                .changeToOrder(any(OrderDto.class));

        //when
        Order order = orderService.registerOrder(new OrderDto());

        //then
        Assertions.assertThat(order.getId()).isEqualTo(1L);

    }

    @Test
    @DisplayName("[성공]주문 : 주문상품 등록 테스트")
    void registerOrderProduct() {

        //given
        doReturn(OrderProduct.builder().id(1L).build())
                .when(orderProductRepository)
                .save(any(OrderProduct.class));

        //when
        OrderProduct orderProduct = orderService.registerOrderProduct(Order.builder().id(1L).build(), Product.builder().id(1L).build());

        //then
        Assertions.assertThat(orderProduct.getId()).isEqualTo(1L);

    }


    @Test
    @DisplayName("[실패]주문 : 주문상품 등록 테스트")
    void registerOrderProductFail1() {
        //when , then
        assertThrows(AssertionError.class,
                ()->  orderService.registerOrderProduct(Order.builder().build(), Product.builder().id(1L).build()));

    }
    @Test
    @DisplayName("[실패]주문 : 주문상품 등록 테스트")
    void registerOrderProductFail2() {
        //when , then
        assertThrows(AssertionError.class,
                ()->  orderService.registerOrderProduct(Order.builder().id(1L).build(), Product.builder().build()));

    }
}
