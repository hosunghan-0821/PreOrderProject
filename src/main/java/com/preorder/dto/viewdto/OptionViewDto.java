package com.preorder.dto.viewdto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.preorder.global.validation.ValidationMarker;
import lombok.*;

import javax.validation.constraints.Null;

import static com.preorder.global.error.ErrorMessage.NULL;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter(AccessLevel.PROTECTED)
@Builder
public class OptionViewDto {

    @Null(groups = {ValidationMarker.OnCreate.class}, message = NULL)
    @Null(groups = {ValidationMarker.OnUpdate.class}, message = NULL)
    private Long id;

    private String name;

    private int fee;

    private String type;

    //Domain : OptionDetail 항목
    @JsonProperty("optionDetailId")
    private Long optionDetailId;
    @JsonProperty("optionValue")
    private String optionValue;

}
