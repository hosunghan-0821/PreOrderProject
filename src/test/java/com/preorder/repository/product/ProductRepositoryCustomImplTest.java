package com.preorder.repository.product;

import com.preorder.config.TestConfiguration;
import com.preorder.domain.Product;
import com.preorder.dto.domaindto.ProductDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;


@Import(TestConfiguration.class)
@ActiveProfiles("local")
@DataJpaTest
class ProductRepositoryCustomImplTest {


    @Autowired
    private ProductRepository productRepository;


    @Test
    @DisplayName("[성공] : 상품 bulk Insert 테스트")
    void getProductList() {
        //given
        Product product1 = Product.builder()
                .productNum(10L)
                .name("마이넘버원1")
                .category("제빵")
                .build();
        productRepository.save(product1);

        Product product2 = Product.builder()
                .productNum(10L)
                .name("마이넘버원2")
                .category("제빵")
                .build();
        productRepository.save(product2);

        Product product3 = Product.builder()
                .productNum(10L)
                .name("마이넘버원3")
                .category("제빵")
                .build();
        productRepository.save(product3);
        Pageable pageable = PageRequest.of(0, 20);
        //when

        Page<ProductDto> productList = productRepository.getProductList(pageable);
        List<ProductDto> content = productList.getContent();
        //then
        Assertions.assertAll(
                () -> content.get(0).getName().equals("마이넘버원3"),
                () -> content.get(1).getName().equals("마이넘버원2"),
                () -> content.get(2).getName().equals("마이넘버원1")
                );
    }

    @Test
    @DisplayName("[성공] : 상품 bulk Insert 테스트")
    void bulkInsertProducts() {

        //given
        List<Product> productList = new ArrayList<>();

        for (int i = 0; i < 1000; i++) {
            Product product = Product.builder()
                    .name("test" + i)
                    .price(123)
                    .build();
            productList.add(product);
        }


        //when
        productRepository.bulkInsertProducts(productList);


        //then
        List<Product> allProductList = productRepository.findAll();

        Assertions.assertEquals(allProductList.size(), 1000);
        for (int i = 0; i < 1000; i++) {
            String name = "test" + i;
            Assertions.assertEquals(true, productList.stream().anyMatch(v -> v.getName().equals(name)));
            ;
        }
    }
}