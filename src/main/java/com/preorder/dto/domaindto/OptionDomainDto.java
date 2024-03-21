package com.preorder.dto.domaindto;


import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter(AccessLevel.PROTECTED)
@Builder
@ToString
public class OptionDomainDto {

    private Long id;

    private String name;

    private int price;

    private String category;
}
