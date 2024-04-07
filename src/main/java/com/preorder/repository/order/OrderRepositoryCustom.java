package com.preorder.repository.order;

import com.preorder.dto.domaindto.OrderDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderRepositoryCustom {

    Page<OrderDto> getProductList(Pageable pageable);
}
