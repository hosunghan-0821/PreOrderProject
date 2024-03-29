package com.preorder.dto.domaindto;


import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter(AccessLevel.PROTECTED)
@Builder
@ToString
public class OptionDto {

    private Long id;

    private String name;

    private int fee;

    private String category;

    private ProductDto productDto;
}
