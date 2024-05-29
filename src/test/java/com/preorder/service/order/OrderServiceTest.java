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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;


@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderMapper orderMapper;
    @Mock
    private OrderProductRepository orderProductRepository;


    @Mock
    private OrderRepository orderRepository;


    @Test
    @DisplayName("[성공] : 주문정보 등록 테스트")
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
    @DisplayName("[성공] : 주문상품 등록 테스트")
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
    @DisplayName("[실패] : 주문상품 등록 테스트")
    void registerOrderProductFail1() {
        //when , then
        assertThrows(AssertionError.class,
                () -> orderService.registerOrderProduct(Order.builder().build(), Product.builder().id(1L).build()));

    }

    @Test
    @DisplayName("[실패] : 주문상품 등록 테스트")
    void registerOrderProductFail2() {
        //when , then
        assertThrows(AssertionError.class,
                () -> orderService.registerOrderProduct(Order.builder().id(1L).build(), Product.builder().build()));

    }

    @Test
    @DisplayName("[성공] : 가능 날짜 확인 테스트")
    void checkReservationDate() {

        LocalTime localTime = Instant.now().atZone(ZoneId.systemDefault()).toLocalTime();

        Instant instant;

        if (localTime.isAfter(LocalTime.of(18, 0))) {
            instant = LocalDate.now().plusDays(3).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
        } else {
            instant = LocalDate.now().plusDays(2).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
        }


        boolean result = orderService.checkReservationDate(instant);
        Assertions.assertThat(result).isEqualTo(true);
    }

    @Test
    @DisplayName("[실패] : 주문 가능 날짜")
    void checkReservationDateFail() {

        LocalTime localTime = Instant.now().atZone(ZoneId.systemDefault()).toLocalTime();

        Instant instant;

        if (localTime.isAfter(LocalTime.of(18, 0))) {
            instant = LocalDate.now().plusDays(2).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
        } else {
            instant = LocalDate.now().plusDays(1).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
        }


        boolean result = orderService.checkReservationDate(instant);
        Assertions.assertThat(result).isEqualTo(false);
    }

    @Test
    @DisplayName("[성공] : 주문id,날짜를 통한 주문정보 조회")
    void getOrder() {
        //given

        doReturn(Arrays.asList(OrderProduct.builder().id(1L).order(Order.builder().clientPhoneNum("01095756210").clientName("hhs").build()).build()))
                .when(orderProductRepository)
                .findOrderProductByClientNameAndClientPhoneNum(any(Long.class), any(String.class), any(String.class));

        //when
        List<OrderProduct> orderProductList = orderService.getOrder(OrderDto.builder().clientName("hhs").clientPhoneNum("01095756210").id(1L).build());
        //then
        Assertions.assertThat(orderProductList.size()).isEqualTo(1);
        Assertions.assertThat(orderProductList.get(0).getOrder().getClientPhoneNum()).isEqualTo("01095756210");
        Assertions.assertThat(orderProductList.get(0).getOrder().getClientName()).isEqualTo("hhs");
    }

    @Test
    @DisplayName("[성공] : 현재날짜를 주문해야하는 항목 조회")
    void getProductToOrder() {

        //given
        doReturn(Arrays.asList(OrderProduct.builder().id(1L).order(Order.builder().clientPhoneNum("01095756210").clientName("hhs").build()).build()))
                .when(orderProductRepository)
                .findOrderByDate(any(Instant.class), any(Instant.class));

        //when
        List<OrderProduct> orderProductList = orderService.getProductToOrder();
        //then

        Assertions.assertThat(orderProductList.size()).isEqualTo(1);
        Assertions.assertThat(orderProductList.get(0).getOrder().getClientPhoneNum()).isEqualTo("01095756210");
        Assertions.assertThat(orderProductList.get(0).getOrder().getClientName()).isEqualTo("hhs");

    }

    @Test
    @DisplayName("[성공] : 주문정보 다중 조회")
    void getOrderList() {

        //given
        doReturn(new PageImpl<>(Arrays.asList(OrderDto.builder().clientPhoneNum("01095756210").clientName("hhs").build())))
                .when(orderRepository)
                .getOrderList(any(Pageable.class));
        Pageable pageable = PageRequest.of(0,20);

        //when
        Page<OrderDto> orderList = orderService.getOrderList(pageable);
        List<OrderDto> content = orderList.getContent();

        //then
        Assertions.assertThat(content.size()).isEqualTo(1);
        Assertions.assertThat(content.get(0).getClientPhoneNum()).isEqualTo("01095756210");
        Assertions.assertThat(content.get(0).getClientName()).isEqualTo("hhs");
    }
}
