package com.preorder.repository;


import com.preorder.domain.Product;
import com.preorder.dto.domaindto.ProductDomainDto;
import com.preorder.dto.mapper.ProductMapper;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

import static com.preorder.domain.QProduct.product;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryCustomImpl implements ProductRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    private final ProductMapper productMapper;

    @Override
    public Page<ProductDomainDto> getProductList(Pageable pageable) {
        final List<Product> productList = jpaQueryFactory.selectFrom(product)
                .orderBy(product.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        int totalCounts = jpaQueryFactory
                .selectFrom(product)
                .orderBy(product.id.desc())
                .fetch().size();


        List<ProductDomainDto> productDomainDtoList = productList.stream()
                .map(productMapper::changeToProductDomainDto)
                .collect(Collectors.toList());

        return new PageImpl<>(productDomainDtoList, pageable, totalCounts);

    }
}
