package com.preorder.repository;

import com.preorder.dto.domaindto.ProductDomainDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductRepositoryCustom {

    Page<ProductDomainDto> getProductList(Pageable pageable);
}
