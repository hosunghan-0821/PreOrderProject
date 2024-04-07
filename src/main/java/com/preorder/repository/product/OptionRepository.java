package com.preorder.repository.product;

import com.preorder.domain.Option;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OptionRepository extends JpaRepository<Option,Long> {

    List<Option> findAllByProductIdOrderById(Long productId);
}
