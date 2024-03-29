package com.preorder.domain;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter(AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tb_option_detail")
public class OptionDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn
    @ManyToOne(fetch = FetchType.LAZY)
    private Option option;

    @JoinColumn
    @ManyToOne(fetch = FetchType.LAZY)
    private OrderProduct orderProduct;

    private String optionValue;

}
