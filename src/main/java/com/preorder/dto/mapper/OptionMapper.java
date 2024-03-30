package com.preorder.dto.mapper;


import com.preorder.domain.Option;
import com.preorder.dto.domaindto.OptionDto;
import com.preorder.dto.viewdto.OptionViewDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface OptionMapper {

    OptionDto changeToOptionDomainDto(OptionViewDto optionViewDto);
    OptionDto changeToOptionDomainDto(Option option);

    Option changeToOption(OptionDto optionDto);

    OptionViewDto changeToOptionViewDto(OptionDto optionDto);

    OptionViewDto changeToOptionViewDto(Option option);

}
