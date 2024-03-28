package com.preorder.service.product;

import com.preorder.domain.Option;
import com.preorder.domain.Product;
import com.preorder.dto.domaindto.OptionDomainDto;
import com.preorder.dto.mapper.OptionMapper;
import com.preorder.repository.OptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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

    public List<OptionDomainDto> findOptionByProduct(Long productById) {
        assert (productById != null && productById > 0);

        List<Option> optionList = optionRepository.findAllByProductIdOrderById(productById);

        return optionList.stream().map(optionMapper::changeToOptionDomainDto).collect(Collectors.toList());

    }
}
