package com.preorder.dto.viewdto;


import com.preorder.global.validation.ValidationMarker.OnCreate;
import com.preorder.global.validation.ValidationMarker.OnUpdate;
import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.util.List;

import static com.preorder.global.error.ErrorMessage.NOT_NULL;
import static com.preorder.global.error.ErrorMessage.NULL;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter(AccessLevel.PROTECTED)
@Builder
public class ProductViewDto {

    @Null(groups = {OnCreate.class}, message = NULL)
    @NotNull(groups = {OnUpdate.class}, message = NOT_NULL)
    private Long id;

    private String name;

    private int price;

    private String category;

    @Valid
    private List<ImageViewDto> images;

    @Valid
    private List<OptionViewDto> options;

    public void setOptionViewDtoList(List<OptionViewDto> optionViewDtoList) {
        this.options = optionViewDtoList;
    }
}
