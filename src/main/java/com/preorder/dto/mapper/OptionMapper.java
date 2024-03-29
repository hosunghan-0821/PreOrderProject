package com.preorder.dto.mapper;


import com.preorder.domain.Option;
import com.preorder.dto.domaindto.OptionDomainDto;
import com.preorder.dto.viewdto.OptionViewDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface OptionMapper {

    OptionDomainDto changeToOptionDomainDto(OptionViewDto optionViewDto);
    OptionDomainDto changeToOptionDomainDto(Option option);

    Option changeToOption(OptionDomainDto optionDomainDto);

    OptionViewDto changeToOptionViewDto(OptionDomainDto optionDomainDto);



}
