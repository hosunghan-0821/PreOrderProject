package com.preorder.dto.viewdto;

import com.preorder.global.validation.ValidationMarker;
import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

import static com.preorder.global.error.ErrorMessage.NOT_NULL;
import static com.preorder.global.error.ErrorMessage.NULL;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter(AccessLevel.PROTECTED)
@Builder
public class ImageViewDto {

    @Null(groups = {ValidationMarker.OnCreate.class}, message = NULL)
    @NotNull(groups = {ValidationMarker.OnUpdate.class}, message = NOT_NULL)
    private Long id;

    private String url;

    //상품 View 순서
    private int order;

}
