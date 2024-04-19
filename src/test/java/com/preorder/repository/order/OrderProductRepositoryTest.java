package com.preorder.repository.order;

import com.preorder.config.TestConfiguration;
import com.preorder.domain.Order;
import com.preorder.domain.OrderProduct;
import com.preorder.domain.Product;
import com.preorder.repository.product.ProductRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Import(TestConfiguration.class)
@DataJpaTest
class OrderProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderProductRepository orderProductRepository;

    @Test
    @DisplayName("[성공] : 주문조회")
    void findOrderProductByClientNameAndClientPhoneNum() {
        //given

        Order order = Order.builder()
                .clientName("한호성")
                .clientPhoneNum("01095756302")
                .reservationDate(Instant.now())
                .build();

        orderRepository.save(order);

        Product product = Product.builder()
                .build();
        productRepository.save(product);

        OrderProduct orderProduct = OrderProduct.builder().
                product(product)
                .order(order)
                .build();

        orderProductRepository.save(orderProduct);

        //when
        List<OrderProduct> orderProductList = orderProductRepository.findOrderProductByClientNameAndClientPhoneNum(order.getId(), "한호성", "01095756302");

        //then
        Assertions.assertThat(orderProductList.size()).isGreaterThan(0);

        Order findOrder = orderProductList.get(0).getOrder();

        Assertions.assertThat(findOrder.getClientName()).isEqualTo("한호성");
        Assertions.assertThat(findOrder.getClientPhoneNum()).isEqualTo("01095756302");


    }

    @Test
    @DisplayName("[성공] : 주문 기간에 따르 조회")
    void findOrderByDate() {
        //given

        LocalDate startDate = LocalDate.of(2024, 4, 19);
        LocalDate endDate = LocalDate.of(2024, 4, 21);
        LocalDate outOfRangeDate = LocalDate.of(2024, 4, 22);
        Instant startInstant = startDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant endInstant = endDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant outOfInstant = outOfRangeDate.atStartOfDay(ZoneId.systemDefault()).toInstant();

        Order order = Order.builder()

                .clientName("한호성")
                .clientPhoneNum("01095756302")
                .reservationDate(startInstant)
                .build();

        Order order2 = Order.builder()
                .clientName("김포성")
                .clientPhoneNum("01095756302")
                .reservationDate(outOfInstant)
                .build();

        orderRepository.save(order);
        orderRepository.save(order2);

        Product product = Product.builder()
                .build();
        productRepository.save(product);

        OrderProduct orderProduct = OrderProduct.builder().
                product(product)
                .order(order)
                .build();

        OrderProduct orderProduct2 = OrderProduct.builder().
                product(product)
                .order(order2)
                .build();

        orderProductRepository.save(orderProduct);
        orderProductRepository.save(orderProduct2);

        //when
        List<OrderProduct> orderProductList = orderProductRepository.findOrderByDate(startInstant, endInstant);

        //then
        Assertions.assertThat(orderProductList.size()).isEqualTo(1);

        Order findOrder = orderProductList.get(0).getOrder();

        Assertions.assertThat(findOrder.getClientName()).isEqualTo("한호성");
        Assertions.assertThat(findOrder.getClientPhoneNum()).isEqualTo("01095756302");

    }
}