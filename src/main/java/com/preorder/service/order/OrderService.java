package com.preorder.service.order;

import com.preorder.domain.Order;
import com.preorder.domain.OrderProduct;
import com.preorder.domain.Product;
import com.preorder.dto.domaindto.OrderDto;
import com.preorder.dto.mapper.OrderMapper;
import com.preorder.dto.mapper.ProductMapper;
import com.preorder.repository.OrderProductRepository;
import com.preorder.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    private final OrderMapper orderMapper;

    private final ProductMapper productMapper;

    private final OrderProductRepository orderProductRepository;

    public Order registerOrder(OrderDto orderDto) {

        assert (orderDto != null);

        Order order = orderMapper.changeToOrder(orderDto);

        return orderRepository.save(order);

    }

    public OrderProduct registerOrderProduct(Order savedOrder, Product product) {


        OrderProduct orderProduct = OrderProduct.builder()
                .order(savedOrder)
                .product(product)
                .build();

        return orderProductRepository.save(orderProduct);

    }

    public List<OrderProduct> getOrder(OrderDto orderDto) {

        return orderProductRepository
                .findOrderProductByClientNameAndClientPhoneNum(orderDto.getId(), orderDto.getClientName(), orderDto.getClientPhoneNum());


    }

    public Page<OrderDto> getOrderList(Pageable pageable) {

        return orderRepository.getProductList(pageable);

    }

    public boolean checkReservationDate(Instant reservationDate) {

        // 현재 날짜
        LocalDate currentDate = Instant.now().atZone(ZoneId.systemDefault()).toLocalDate();

        // 주문 날짜에 추가할 일 수 결정
        int daysToAdd = 1; // 기본적으로 +2일 // 비교를 초과로 하기 때문에 + 1

        // 현재 시간이 오후 6시 이후라면 추가로 1일 더함
        if (LocalTime.now().isAfter(LocalTime.of(18, 0))) {
            daysToAdd++;
        }

        // 주문 날짜가 오늘로부터 지정된 일 수 이후인지 확인
        LocalDate specifiedDaysAfterCurrent = currentDate.plusDays(daysToAdd);
        LocalDate reservationLocalDate = reservationDate.atZone(ZoneId.systemDefault()).toLocalDate();
        if (reservationLocalDate.isAfter(specifiedDaysAfterCurrent)) {
          return true;
        }
        return false;

    }

    public List<OrderProduct> getProductToOrder() {
        LocalDate currentDate = Instant.now().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate findOrderDate = currentDate.plusDays(2);

        Instant startDate = findOrderDate.atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant endDate = findOrderDate.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC);

        return orderProductRepository.findOrderByDate(startDate, endDate);
    }
}
