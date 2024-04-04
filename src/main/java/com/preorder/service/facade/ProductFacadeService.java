package com.preorder.service.facade;


import com.preorder.domain.Product;
import com.preorder.dto.domaindto.OptionDto;
import com.preorder.dto.domaindto.ProductDto;
import com.preorder.dto.mapper.OptionMapper;
import com.preorder.dto.mapper.ProductMapper;
import com.preorder.dto.viewdto.OptionViewDto;
import com.preorder.dto.viewdto.ProductViewDto;
import com.preorder.global.util.ProductExcelReader;
import com.preorder.service.product.OptionService;
import com.preorder.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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

    private final ProductExcelReader productExcelReader;

    @Transactional
    public void registerProduct(ProductViewDto productViewDto) {

        assert (productViewDto != null);

        // productViewDto -> productDomainDto 후 등록
        // To-DO 전환 잘 되는지 TestCode 작성
        ProductDto productDto = productMapper.changeToProductDomainDto(productViewDto);

        assert (productDto != null);

        //productDomainDto -> product save
        Product registeredProduct = productService.register(productDto);
        assert (registeredProduct != null);

        // productViewDto -> OptionDomainDto 후 등록
        // To-DO 전환 잘 되는지 TestCode 작성
        List<OptionDto> optionDtoList = new ArrayList<>();

        for (OptionViewDto optionViewDto : productViewDto.getOptions()) {
            optionDtoList.add(optionMapper.changeToOptionDomainDto(optionViewDto));
        }

        for (OptionDto optionDto : optionDtoList) {
            optionService.register(optionDto, registeredProduct);
        }

    }

    @Transactional(readOnly = true)
    public Page<ProductViewDto> getProductList(Pageable pageable) {

        assert (pageable != null);

        Page<ProductDto> productList = productService.getProductList(pageable);
        List<ProductViewDto> productViewDtoList = productList.stream().map(productMapper::changeToProductViewDto).collect(Collectors.toList());

        return new PageImpl<>(productViewDtoList, pageable, productList.getTotalElements());
    }

    @Transactional(readOnly = true)
    public ProductViewDto getProductDetail(Long id) {

        assert (id != null && id > 0);

        ProductDto productDto = productService.getProductById(id);
        ProductViewDto productViewDto = productMapper.changeToProductViewDto(productDto);

        assert (productDto != null);

        List<OptionDto> optionByProduct = optionService.findOptionByProduct(id);

        List<OptionViewDto> optionViewDtoList = optionByProduct.stream()
                .map(optionMapper::changeToOptionViewDto)
                .collect(Collectors.toList());

        productViewDto.setOptionViewDtoList(optionViewDtoList);

        return productViewDto;
    }

    @Transactional
    public void updateProduct(ProductViewDto productViewDto) {
        assert (productViewDto != null);
        assert (productViewDto.getId() != null);

        ProductDto productDto = productMapper.changeToProductDomainDto(productViewDto);
        Product updateProduct = productService.updateProduct(productDto);

        List<OptionDto> optionDtoList = new ArrayList<>();

        for (OptionViewDto optionViewDto : productViewDto.getOptions()) {
            optionDtoList.add(optionMapper.changeToOptionDomainDto(optionViewDto));
        }

        //제거 후 옵션 재생성
        optionService.deleteAllOption(updateProduct.getId());

        for (OptionDto optionDto : optionDtoList) {
            optionService.register(optionDto, updateProduct);
        }

    }

    public void deleteProduct(Long id) {
        assert (id != null && id > 0);

        //순서중요
        optionService.deleteAllOption(id);
        productService.deleteProduct(id);

    }

    @Transactional
    public void bulkRegisterProduct(MultipartFile file) {

        List<ProductDto> productDtoList = productExcelReader.getProductListFromExcel(file);
        productService.bulkRegisterProduct(productDtoList);

    }



}
