package com.preorder.dto.mapper;

import com.preorder.dto.domaindto.ProductDomainDto;
import com.preorder.dto.domaindto.ProductDomainDto.ProductDomainDtoBuilder;
import com.preorder.dto.viewdto.ProductViewDto;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-03-21T17:13:48+0900",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 11.0.22 (Azul Systems, Inc.)"
)
@Component
public class ProductMapperImpl implements ProductMapper {

    @Override
    public ProductDomainDto changeToProductDomainDto(ProductViewDto productViewDto) {
        if ( productViewDto == null ) {
            return null;
        }

        ProductDomainDtoBuilder productDomainDto = ProductDomainDto.builder();

        productDomainDto.id( productViewDto.getId() );
        productDomainDto.name( productViewDto.getName() );
        productDomainDto.price( productViewDto.getPrice() );
        productDomainDto.category( productViewDto.getCategory() );

        return productDomainDto.build();
    }
}
