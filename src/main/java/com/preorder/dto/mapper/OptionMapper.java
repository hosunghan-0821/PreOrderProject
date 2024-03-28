package com.preorder.dto.mapper;


import com.preorder.domain.Option;
import com.preorder.dto.domaindto.OptionDomainDto;
import com.preorder.dto.viewdto.OptionViewDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OptionMapper {

    OptionDomainDto changeToOptionDomainDto(OptionViewDto optionViewDto);

    Option changeToOption(OptionDomainDto optionDomainDto);

}
