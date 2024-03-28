package com.preorder.service.product;


import com.preorder.domain.Product;
import com.preorder.dto.domaindto.ProductDomainDto;
import com.preorder.dto.mapper.ProductMapper;
import com.preorder.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    private final ProductMapper mapper;

    public Product register(ProductDomainDto productDomainDto) {

        assert (productDomainDto != null);

        Product product = mapper.changeToProduct(productDomainDto);

        return productRepository.save(product);

    }
}
