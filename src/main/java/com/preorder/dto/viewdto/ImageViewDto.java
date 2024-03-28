package com.preorder.dto.viewdto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter(AccessLevel.PROTECTED)
@Builder
public class ImageViewDto {


    private Long id;

    private String url;

    //상품 View 순서
    private int order;

}
