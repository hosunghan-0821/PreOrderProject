package com.preorder.service.product;

import com.preorder.domain.Option;
import com.preorder.domain.Product;
import com.preorder.dto.domaindto.OptionDto;
import com.preorder.dto.mapper.OptionMapper;
import com.preorder.repository.product.OptionRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;


@ExtendWith(MockitoExtension.class)
class OptionServiceTest {

    @InjectMocks
    private OptionService optionService;

    @Mock
    private OptionRepository optionRepository;

    @Spy
    private OptionMapper optionMapper = Mappers.getMapper(OptionMapper.class);

    @Test
    @DisplayName("[실패] : 옵션 저장")
    void registerOptionFail() {
        //when - then
        assertThrows(AssertionError.class, () -> optionService.register(null, new Product()));
        assertThrows(AssertionError.class, () -> optionService.register(new OptionDto(), null));
    }

    @Test
    @DisplayName("[성공] : 옵션 저장")
    void registerOption() {
        //given
        OptionDto optionDto = OptionDto.builder().name("초").fee(100).category("기본").build();
        Product product = Product.builder().id(1L).build();
        Option option = optionMapper.changeToOption(optionDto);
        doReturn(option).when(optionRepository).save(any());

        //when
        Option registedOption = optionService.register(optionDto, product);

        //then
        Assertions.assertAll(
                () -> assertThat(registedOption.getName()).isEqualTo("초"),
                () -> assertThat(registedOption.getFee()).isEqualTo(100)
        );
    }

    @Test
    @DisplayName("[성공] : 옵션 조회")
    void findOptionByProduct() {
        //given
        OptionDto optionDto = OptionDto.builder().name("초").fee(100).category("기본").build();
        Option option = optionMapper.changeToOption(optionDto);
        doReturn(Arrays.asList(option)).when(optionRepository).findAllByProductIdOrderById(any(Long.class));

        //when
        List<OptionDto> optionDtoList = optionService.findOptionByProduct(1L);

        //then
        Assertions.assertAll(
                () -> assertThat(optionDtoList.get(0).getName()).isEqualTo("초"),
                () -> assertThat(optionDtoList.get(0).getFee()).isEqualTo(100)
        );
    }

    @Test
    @DisplayName("[실패] : 옵션 조회")
    void findOptionByProductFail() {
        //when - then
        assertThrows(AssertionError.class, () -> optionService.findOptionByProduct(0L));
        assertThrows(AssertionError.class, () -> optionService.findOptionByProduct(null));
    }


    @Test
    @DisplayName("[실패] : 옵션 제거")
    void deleteAllOptionFail() {

        //when,then
        assertThrows(AssertionError.class, () -> optionService.findOptionByProduct(null));

    }

    @Test
    @DisplayName("[성공] : 옵션 제거")
    void deleteAllOption() {

        //given
        OptionDto optionDto = OptionDto.builder().name("초").fee(100).category("기본").build();
        Option option = optionMapper.changeToOption(optionDto);
        doReturn(Arrays.asList(option)).when(optionRepository).findAllByProductIdOrderById(any(Long.class));

        //when
        optionService.deleteAllOption(1L);
        //then
        assert (true);
    }
}