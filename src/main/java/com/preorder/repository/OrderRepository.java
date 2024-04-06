package com.preorder.repository;

import com.preorder.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long>, OrderRepositoryCustom {
    Optional<Order> findOrderByClientNameAndClientPhoneNum(String clientName, String clientPhoneNum);


}
