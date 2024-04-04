package com.preorder.service.product;

import com.preorder.domain.Option;
import com.preorder.domain.OptionDetail;
import com.preorder.domain.OrderProduct;
import com.preorder.domain.Product;
import com.preorder.dto.domaindto.OptionDto;
import com.preorder.dto.mapper.OptionMapper;
import com.preorder.global.error.exception.NotFoundException;
import com.preorder.repository.OptionDetailRepository;
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

    private final OptionDetailRepository optionDetailRepository;

    public Option register(OptionDto optionDto, Product registeredProduct) {

        assert (optionDto != null);
        assert (registeredProduct != null);

        Option option = optionMapper.changeToOption(optionDto);
        option.registerProduct(registeredProduct);

        return optionRepository.save(option);

    }

    public List<OptionDto> findOptionByProduct(Long productById) {
        assert (productById != null && productById > 0);

        List<Option> optionList = optionRepository.findAllByProductIdOrderById(productById);

        return optionList.stream().map(optionMapper::changeToOptionDomainDto).collect(Collectors.toList());

    }

    public void deleteAllOption(Long id) {

        assert (id != null);

        List<Option> optionList = optionRepository.findAllByProductIdOrderById(id);
        optionRepository.deleteAllById(optionList.stream().map(Option::getId).collect(Collectors.toList()));


    }

    public Option isExistOption(Long optionId) {

        assert (optionId != null);
        assert (optionId > 0);

        return optionRepository.findById(optionId)
                .orElseThrow(NotFoundException::new);
    }

    public void saveOptionDetail(OrderProduct orderProduct, Option option, String optionValue) {

        OptionDetail optionDetail = OptionDetail.builder()
                .orderProduct(orderProduct)
                .option(option)
                .optionValue(optionValue).build();

        optionDetailRepository.save(optionDetail);
    }
    public boolean matchProductAndOption(Option option, Long productId) {
        assert (option != null && option.getId() != null);

        if(option.getProduct().getId().equals(productId)){
            return  true;
        }
        return  false;
    }

}
