package com.preorder.repository.product;

import com.preorder.domain.Product;
import com.preorder.dto.domaindto.ProductDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductRepositoryCustom {

    Page<ProductDto> getProductList(Pageable pageable);

    void bulkInsertProducts(List<Product> productList);
}
