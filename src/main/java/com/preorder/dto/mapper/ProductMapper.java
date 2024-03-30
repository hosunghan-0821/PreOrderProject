package com.preorder.dto.mapper;


import com.preorder.domain.Product;
import com.preorder.dto.domaindto.ProductDto;
import com.preorder.dto.viewdto.ProductViewDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface ProductMapper {


    ProductDto changeToProductDomainDto(ProductViewDto productViewDto);
    Product changeToProduct(ProductDto productDto);

    ProductViewDto changeToProductViewDto(ProductDto productDto);
    ProductViewDto changeToProductViewDto(Product product);
    ProductDto changeToProductDomainDto(Product product);
}
