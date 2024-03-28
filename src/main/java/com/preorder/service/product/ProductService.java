package com.preorder.service.product;


import com.preorder.domain.Product;
import com.preorder.dto.domaindto.ProductDomainDto;
import com.preorder.dto.mapper.ProductMapper;
import com.preorder.repository.ProductRepository;
import com.preorder.repository.ProductRepositoryCustom;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    private final ProductRepositoryCustom productRepositoryCustom;

    private final ProductMapper mapper;

    public Product register(ProductDomainDto productDomainDto) {

        assert (productDomainDto != null);

        Product product = mapper.changeToProduct(productDomainDto);

        return productRepository.save(product);

    }

    public Page<ProductDomainDto> getProductList(Pageable pageable) {

        return productRepositoryCustom.getProductList(pageable);
    }

    public ProductDomainDto getProductById(Long id) {
        assert (id != null && id > 0);
        Product product = productRepository.findById(id).orElseThrow(IllegalArgumentException::new);
        return mapper.changeToProductDomainDto(product);
    }

    public Product updateProduct(ProductDomainDto productDomainDto) {
        assert (productDomainDto != null);
        assert (productDomainDto.getId() != null);

        Product product = productRepository.findById(productDomainDto.getId()).orElseThrow(IllegalArgumentException::new);

        product.updateData(productDomainDto.getName(), product.getCategory(), product.getPrice());

        return product;
    }


    public void deleteProduct(Long id) {
        assert (id != null && id > 0);

        productRepository.deleteById(id);


    }
}
