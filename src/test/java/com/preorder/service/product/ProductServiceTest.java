package com.preorder.service.product;

import com.preorder.domain.Product;
import com.preorder.dto.domaindto.ProductDto;
import com.preorder.dto.mapper.ProductMapper;
import com.preorder.repository.product.ProductRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {


    @InjectMocks
    private  ProductService productService;

    @Mock
    private  ProductRepository productRepository;

    @Spy
    private ProductMapper productMapper =  Mappers.getMapper(ProductMapper.class);

    @Test
    @DisplayName("[실패] : 상품등록 테스트")
    void registerFail() {

        //when - then
        assertThrows(AssertionError.class,() -> productService.register(null));

    }

    @Test
    @DisplayName("[실패] : 상품 아이디로 조회")
    void getProductByIdFail() {
        //when - then
        assertThrows(AssertionError.class, () -> productService.getProductById(null));
        assertThrows(AssertionError.class, () -> productService.getProductById(0L));
    }
    @Test
    @DisplayName("[성공] : 상품 아이디로 조회")
    void getProductById() {

        //given
        Product product = Product.builder().id(1L).build();
        Mockito.when(productRepository.findById(any(Long.class))).thenReturn(Optional.of(product));

        // when
        ProductDto productDto = productService.getProductById(product.getId());

        // then
        Assertions.assertThat(productDto.getId()).isEqualTo(product.getId());
    }

    @Test
    @DisplayName("[성공] : 상품 정보 업데이트")
    void updateProduct() {
        //given
        ProductDto updateProductDto = ProductDto.builder()
                .id(1L)
                .price(1000)
                .category("카테고리")
                .name("마이넘버원")
                .build();

        Product product = Product.builder()
                .id(1L)
                .price(2000)
                .name("수정")
                .build();

        Mockito.when(productRepository.findById(any(Long.class))).thenReturn(Optional.of(product));

        //when
        Product updateProduct = productService.updateProduct(updateProductDto);

        //then
        Assertions.assertThat(updateProduct.getId()).isEqualTo(updateProductDto.getId());
        Assertions.assertThat(updateProduct.getCategory()).isEqualTo(updateProductDto.getCategory());
        Assertions.assertThat(updateProduct.getName()).isEqualTo(updateProductDto.getName());
        Assertions.assertThat(updateProduct.getPrice()).isEqualTo(updateProductDto.getPrice());

    }
}