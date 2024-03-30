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
}
