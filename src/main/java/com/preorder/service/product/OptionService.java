package com.preorder.service.product;

import com.preorder.domain.Option;
import com.preorder.domain.Product;
import com.preorder.dto.domaindto.OptionDomainDto;
import com.preorder.dto.mapper.OptionMapper;
import com.preorder.repository.OptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OptionService {

    private final OptionRepository optionRepository;

    private final OptionMapper optionMapper;
    public Option register(OptionDomainDto optionDomainDto, Product registeredProduct) {

        assert (optionDomainDto != null);
        assert (registeredProduct != null);

        Option option = optionMapper.changeToOption(optionDomainDto);
        option.registerProduct(registeredProduct);

        return optionRepository.save(option);

    }
}
