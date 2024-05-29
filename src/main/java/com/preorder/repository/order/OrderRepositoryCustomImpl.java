package com.preorder.repository.order;

import com.preorder.domain.Order;
import com.preorder.dto.domaindto.OrderDto;
import com.preorder.dto.mapper.OrderMapper;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

import static com.preorder.domain.QOrder.order;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryCustomImpl implements OrderRepositoryCustom {


    private final JPAQueryFactory jpaQueryFactory;

    private final OrderMapper orderMapper;
    @Override
    public Page<OrderDto> getOrderList(Pageable pageable) {

        List<Order> orderList = jpaQueryFactory.selectFrom(order)
                .orderBy(order.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();


        int totalCounts = jpaQueryFactory
                .selectFrom(order)
                .orderBy(order.id.desc())
                .fetch().size();

        List<OrderDto> orderDtoList = orderList.stream()
                .map(orderMapper::changeToOrderDto)
                .collect(Collectors.toList());

        return new PageImpl<>(orderDtoList, pageable, totalCounts);

    }
}
