package com.preorder.dto.mapper;

import com.preorder.dto.domaindto.OptionDomainDto;
import com.preorder.dto.domaindto.OptionDomainDto.OptionDomainDtoBuilder;
import com.preorder.dto.viewdto.OptionViewDto;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-03-21T17:13:48+0900",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 11.0.22 (Azul Systems, Inc.)"
)
@Component
public class OptionMapperImpl implements OptionMapper {

    @Override
    public OptionDomainDto changeToOptionDomainDto(OptionViewDto optionViewDto) {
        if ( optionViewDto == null ) {
            return null;
        }

        OptionDomainDtoBuilder optionDomainDto = OptionDomainDto.builder();

        optionDomainDto.id( optionViewDto.getId() );
        optionDomainDto.name( optionViewDto.getName() );

        return optionDomainDto.build();
    }
}
