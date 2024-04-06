package com.preorder.repository;

import com.preorder.domain.OrderProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface OrderProductRepository extends JpaRepository<OrderProduct,Long> {


    @Query(value = "select op from OrderProduct as op " +
            "left join fetch op.product as p " +
            "left join fetch op.order as o " +
            "where op.order.id = :id " +
            "and o.clientName =:clientName " +
            "and o.clientPhoneNum = :clientPhoneNum ")
    List<OrderProduct> findOrderProductByClientNameAndClientPhoneNum(@Param("id") Long id,
                                                                     @Param("clientName") String clientName,
                                                                     @Param("clientPhoneNum") String clientPhoneNum);



    @Query(value = "select op from OrderProduct as op " +
            "left join fetch op.product as p " +
            "left join fetch op.order as o " +
            "where op.order.reservationDate >= :startDate " +
            "and op.order.reservationDate < :endDate ")
    List<OrderProduct> findOrderByDate(@Param("startDate") Instant startDate, @Param("endDate") Instant endDate);


}
