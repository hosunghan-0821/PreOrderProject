package com.preorder.dto.mapper;

import com.preorder.domain.Option;
import com.preorder.domain.Option.OptionBuilder;
import com.preorder.dto.domaindto.OptionDomainDto;
import com.preorder.dto.domaindto.OptionDomainDto.OptionDomainDtoBuilder;
import com.preorder.dto.viewdto.OptionViewDto;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-03-28T11:27:43+0900",
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

    @Override
    public Option changeToOption(OptionDomainDto optionDomainDto) {
        if ( optionDomainDto == null ) {
            return null;
        }

        OptionBuilder option = Option.builder();

        option.id( optionDomainDto.getId() );
        option.name( optionDomainDto.getName() );

        return option.build();
    }
}
