package com.preorder.repository;

import com.preorder.domain.Product;
import com.preorder.dto.domaindto.ProductDomainDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductRepositoryCustom {

    Page<ProductDomainDto> getProductList(Pageable pageable);

    void bulkInsertProducts(List<Product> productList);
}
