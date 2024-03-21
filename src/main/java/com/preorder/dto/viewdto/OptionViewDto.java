package com.preorder.dto.viewdto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter(AccessLevel.PROTECTED)
@Builder
public class OptionViewDto {

    private Long id;

    private String name;

    private int fee;

    private String type;

}
