package com.preorder.repository;


import com.preorder.domain.Product;
import com.preorder.dto.domaindto.ProductDto;
import com.preorder.dto.mapper.ProductMapper;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import static com.preorder.domain.QProduct.product;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryCustomImpl implements ProductRepositoryCustom{

    private final JPAQueryFactory jpaQueryFactory;

    private final ProductMapper productMapper;

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Page<ProductDto> getProductList(Pageable pageable) {
        final List<Product> productList = jpaQueryFactory.selectFrom(product)
                .orderBy(product.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        int totalCounts = jpaQueryFactory
                .selectFrom(product)
                .orderBy(product.id.desc())
                .fetch().size();


        List<ProductDto> productDtoList = productList.stream()
                .map(productMapper::changeToProductDomainDto)
                .collect(Collectors.toList());

        return new PageImpl<>(productDtoList, pageable, totalCounts);

    }

    @Override
    public void bulkInsertProducts(List<Product> productList) {


        String sql = "INSERT INTO product " +
                "(name,price,category,is_deleted) VALUES (?,?,?,?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Product product = productList.get(i);

                ps.setString(1, product.getName());
                ps.setInt(2, product.getPrice());
                ps.setString(3, product.getCategory());
                ps.setBoolean(4, false);
            }

            @Override
            public int getBatchSize() {
                return productList.size();
            }
        });


    }
}
