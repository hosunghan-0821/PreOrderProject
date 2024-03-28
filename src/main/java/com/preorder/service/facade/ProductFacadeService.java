package com.preorder.service.facade;


import com.preorder.domain.Product;
import com.preorder.dto.domaindto.OptionDomainDto;
import com.preorder.dto.domaindto.ProductDomainDto;
import com.preorder.dto.mapper.OptionMapper;
import com.preorder.dto.mapper.ProductMapper;
import com.preorder.dto.viewdto.OptionViewDto;
import com.preorder.dto.viewdto.ProductViewDto;
import com.preorder.service.product.OptionService;
import com.preorder.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductFacadeService {

    private final ProductService productService;

    private final OptionService optionService;

    private final ProductMapper productMapper;

    private final OptionMapper optionMapper;

    @Transactional
    public boolean registerProduct(ProductViewDto productViewDto) {

        assert (productViewDto != null);

        // productViewDto -> productDomainDto 후 등록
        // To-DO 전환 잘 되는지 TestCode 작성
        ProductDomainDto productDomainDto = productMapper.changeToProductDomainDto(productViewDto);

        assert (productDomainDto != null);

        //productDomainDto -> product save
        Product registeredProduct = productService.register(productDomainDto);
        assert (registeredProduct != null);

        // productViewDto -> OptionDomainDto 후 등록
        // To-DO 전환 잘 되는지 TestCode 작성
        List<OptionDomainDto> optionDomainDtoList = new ArrayList<>();

        for (OptionViewDto optionViewDto : productViewDto.getOptions()) {
            optionDomainDtoList.add(optionMapper.changeToOptionDomainDto(optionViewDto));
        }

        for (OptionDomainDto optionDomainDto : optionDomainDtoList) {
            optionService.register(optionDomainDto, registeredProduct);
        }

        //image 저장 나중에

        return true;
    }

    public Page<ProductViewDto> getProductList(Pageable pageable) {

        assert (pageable != null);

        Page<ProductDomainDto> productList = productService.getProductList(pageable);
        List<ProductViewDto> productViewDtoList = productList.stream().map(productMapper::changeToProductViewDto).collect(Collectors.toList());

        return new PageImpl<>(productViewDtoList, pageable, productList.getTotalElements());
    }

    public ProductViewDto getProductDetail(Long id) {

        assert (id != null && id > 0);

        ProductDomainDto productDomainDto = productService.getProductById(id);

        ProductViewDto productViewDto = productMapper.changeToProductViewDto(productDomainDto);
        assert (productDomainDto != null);

        List<OptionDomainDto> optionByProduct = optionService.findOptionByProduct(id);

        List<OptionViewDto> optionViewDtoList = optionByProduct.stream()
                .map(optionMapper::changeToOptionViewDto)
                .collect(Collectors.toList());

        productViewDto.setOptionViewDtoList(optionViewDtoList);

        return productViewDto;
    }
}
