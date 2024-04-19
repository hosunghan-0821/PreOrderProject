package com.preorder.repository.order;

import com.preorder.config.TestConfiguration;
import com.preorder.domain.Order;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.Instant;
import java.util.Optional;

@Import(TestConfiguration.class)
@DataJpaTest
public class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Test
    @DisplayName("주문 테스트")
    void registerOrder(){
        //given
        Order order = Order.builder()
                .clientName("한호성")
                .clientPhoneNum("01095756302")
                .reservationDate(Instant.now())
                .build();

        //when
        orderRepository.save(order);

        //then
        Assertions.assertThat(order.getClientName()).isEqualTo("한호성");
    }

    @Test
    @DisplayName("주문조회 테스트")
    void findOrder(){
        //given

        Order order = Order.builder()
                .clientName("한호성")
                .clientPhoneNum("01095756302")
                .reservationDate(Instant.now())
                .build();

        orderRepository.save(order);

        //when
        Optional<Order> findOrder = orderRepository.findOrderByClientNameAndClientPhoneNum("한호성", "01095756302");


        Assertions.assertThat(findOrder.isPresent()).isEqualTo(true);

        //then
        //noinspection OptionalGetWithoutIsPresent
        Assertions.assertThat(findOrder.get().getClientName()).isEqualTo("한호성");
        Assertions.assertThat(findOrder.get().getClientPhoneNum()).isEqualTo("01095756302");


    }



}
