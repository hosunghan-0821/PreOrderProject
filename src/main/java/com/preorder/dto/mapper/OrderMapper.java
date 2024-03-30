package com.preorder.dto.mapper;


import com.preorder.domain.Order;
import com.preorder.dto.domaindto.OrderDto;
import com.preorder.dto.viewdto.OrderViewDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface OrderMapper {
    OrderDto changeTOoOrderDomainDto(OrderViewDto orderViewDto);

    Order changeToOrder(OrderDto orderDto);

    OrderDto changeToOrderDto(Order order);

    OrderViewDto toOrderViewDto(Order order);

    OrderViewDto toOrderViewDto(OrderDto orderDto);
}
