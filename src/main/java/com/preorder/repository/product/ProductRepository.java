package com.preorder.repository.product;

import com.preorder.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.LockModeType;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long>, ProductRepositoryCustom {

    @Query(value = "SELECT p from Product p " +
            "WHERE p.id IN :ids")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Product> findAllByIdWithLock(@Param(value = "ids") List<Long> orderProductIdList);
}
