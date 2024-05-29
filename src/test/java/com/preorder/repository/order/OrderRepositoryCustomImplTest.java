package com.preorder.repository.order;

import com.preorder.config.TestConfiguration;
import com.preorder.domain.Order;
import com.preorder.dto.domaindto.OrderDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Import(TestConfiguration.class)
@ActiveProfiles("local")
@DataJpaTest
class OrderRepositoryCustomImplTest {

    @Autowired
    private OrderRepository orderRepository;

    @Test
    @DisplayName("[성공] : 주문 다중 조회")
    void getOrderList() {
        //given
        Pageable pageable = PageRequest.of(0, 20);

        Order order1 = Order.builder()
                .clientName("한호성")
                .clientPhoneNum("01095756302")
                .reservationDate(Instant.now())
                .build();

        orderRepository.save(order1);

        Order order2 = Order.builder()
                .clientName("김포성")
                .clientPhoneNum("01095756302")
                .reservationDate(Instant.now())
                .build();

        orderRepository.save(order2);

        Order order3 = Order.builder()
                .clientName("이포성")
                .clientPhoneNum("01095756302")
                .reservationDate(Instant.now())
                .build();

        orderRepository.save(order3);

        //when
        Page<OrderDto> orderList = orderRepository.getOrderList(pageable);

        //then
        List<OrderDto> content = orderList.getContent();
        assertEquals(3, content.size());
        assertAll(
                () -> {
                    assertAll(
                            () -> assertEquals("이포성", content.get(0).getClientName())
                    );
                },
                () -> {
                    assertAll(

                            () -> assertEquals("김포성", content.get(1).getClientName())
                    );
                },
                () -> {
                    assertAll(

                            () -> assertEquals("한호성", content.get(2).getClientName())
                    );
                }
        );
    }
}