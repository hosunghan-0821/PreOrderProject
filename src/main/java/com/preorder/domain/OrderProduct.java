package com.preorder.domain;


import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter(AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tb_order_product")
@ToString
public class OrderProduct{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn
    @ManyToOne(fetch = FetchType.LAZY)
    private Order order;

    @JoinColumn(name = "product_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Product product;

    @OneToMany(mappedBy = "orderProduct")
    private List<OptionDetail> optionDetails;
}
