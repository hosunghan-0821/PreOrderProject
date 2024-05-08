package com.preorder.service.product;


import com.preorder.domain.Product;
import com.preorder.dto.domaindto.ProductDto;
import com.preorder.dto.mapper.ProductMapper;
import com.preorder.global.cache.CacheString;
import com.preorder.global.error.dto.ErrorCode;
import com.preorder.global.error.exception.InvalidArgumentException;
import com.preorder.repository.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    private final ProductMapper mapper;


    @CacheEvict(value = CacheString.PRODUCT_CACHE, allEntries = true)
    public Product register(ProductDto productDto) {

        assert (productDto != null);

        Product product = mapper.changeToProduct(productDto);

        return productRepository.save(product);

    }

    public Page<ProductDto> getProductList(Pageable pageable) {

        return productRepository.getProductList(pageable);
    }

    public ProductDto getProductById(Long id) {
        assert (id != null && id > 0);
        Product product = productRepository.findById(id).orElseThrow(() -> new InvalidArgumentException(ErrorCode.INVALID_ARGUMENT_EXCEPTION));
        return mapper.changeToProductDomainDto(product);
    }
    @CacheEvict(value = CacheString.PRODUCT_CACHE, allEntries = true)
    public Product updateProduct(ProductDto productDto) {
        assert (productDto != null);
        assert (productDto.getId() != null);

        Product product = productRepository.findById(productDto.getId()).orElseThrow(InvalidArgumentException::new);

        product.updateData(productDto.getName(), productDto.getCategory(), productDto.getPrice());

        return product;
    }


    @CacheEvict(value = CacheString.PRODUCT_CACHE, allEntries = true)
    public void deleteProduct(Long id) {
        assert (id != null && id > 0);

        productRepository.deleteById(id);

    }

    @CacheEvict(value = CacheString.PRODUCT_CACHE, allEntries = true)
    public void bulkRegisterProduct(List<ProductDto> productDtoList) {

        List<Product> productList = productDtoList.stream().map(mapper::changeToProduct).collect(Collectors.toList());
        productRepository.bulkInsertProducts(productList);

    }

    public Product isExistProduct(Long productId) {

        assert (productId != null);
        assert (productId > 0);

        return productRepository.findById(productId)
                .orElseThrow(InvalidArgumentException::new); // 오류처리 변경해야함

    }
}
