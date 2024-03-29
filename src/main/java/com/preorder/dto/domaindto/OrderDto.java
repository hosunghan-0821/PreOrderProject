package com.preorder.dto.domaindto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.Instant;


@Getter
@Setter(AccessLevel.PROTECTED)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("clientName")
    private String clientName;

    @JsonProperty("clientPhoneNum")
    private String clientPhoneNum;

    @JsonProperty("reservationDate")
    private Instant reservationDate;

}
