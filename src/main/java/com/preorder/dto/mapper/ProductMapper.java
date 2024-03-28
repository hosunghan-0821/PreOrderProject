package com.preorder.dto.mapper;


import com.preorder.domain.Product;
import com.preorder.dto.domaindto.ProductDomainDto;
import com.preorder.dto.viewdto.ProductViewDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface ProductMapper {


    ProductDomainDto changeToProductDomainDto(ProductViewDto productViewDto);
    Product changeToProduct(ProductDomainDto productDomainDto);


}
