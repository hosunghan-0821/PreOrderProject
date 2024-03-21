package com.preorder.service.product;


import com.preorder.dto.domaindto.OptionDomainDto;
import com.preorder.dto.domaindto.ProductDomainDto;
import com.preorder.dto.mapper.OptionMapper;
import com.preorder.dto.mapper.ProductMapper;
import com.preorder.dto.viewdto.OptionViewDto;
import com.preorder.dto.viewdto.ProductViewDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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

        // productViewDto -> OptionDomainDto 후 등록
        // To-DO 전환 잘 되는지 TestCode 작성
        List<OptionDomainDto> optionDomainDtoList = new ArrayList<>();
        for (OptionViewDto optionViewDto : productViewDto.getOptions()) {
            optionDomainDtoList.add(optionMapper.changeToOptionDomainDto(optionViewDto));
        }






        return true;
    }

    public List<ProductViewDto> getProductDto(Pageable pageable) {

        assert (pageable != null);
        return null;
    }
}
