package com.preorder.dto.viewdto;


import com.preorder.global.validation.ValidationMarker.OnUpdate;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.List;

import static com.preorder.global.error.ErrorMessage.NOT_NULL;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter(AccessLevel.PROTECTED)
@Builder
public class ProductViewDto {


    @NotNull(groups = {OnUpdate.class},message = NOT_NULL)
    private Long id;

    private String name;

    private int price;

    private String category;

    private List<ImageViewDto> images;

    private List<OptionViewDto> options;

}
