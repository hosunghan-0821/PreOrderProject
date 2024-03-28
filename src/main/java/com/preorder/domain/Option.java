package com.preorder.domain;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter(AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Option extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private int fee;

    private String optionType;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    private Product product;

    public void registerProduct(Product product) {
        this.product = product;
    }
}
