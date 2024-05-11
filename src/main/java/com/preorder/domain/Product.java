package com.preorder.domain;

import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;

@Entity
@Getter
@Setter(AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tb_product")
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String category;

    private int price;


    @ColumnDefault("0")
    @Column(nullable = false)
    private Long productNum;


    public void updateData(String name, String category, int price) {
        this.name = name;
        this.category = category;
        this.price = price;
    }

    @PrePersist
    public void prePersist() {
        this.productNum = this.productNum == null ? 0L : this.productNum;
    }

    public void updateProductNum(Long orderNum) {
        this.productNum -= orderNum;
    }
}
