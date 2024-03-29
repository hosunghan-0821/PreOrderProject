package com.preorder.dto.viewdto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.List;

import static com.preorder.global.error.ErrorMessage.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter(AccessLevel.PROTECTED)
@Builder
@ToString
public class OrderViewDto {

    private Long id;

    @NotNull(message = NOT_NULL_CLIENT_NAME)
    @JsonProperty("clientName")
    private String clientName;

    @NotNull(message = NOT_NULL_CLIENT_PHONE_NUM)
    @JsonProperty("clientPhoneNum")
    private String clientPhoneNum;

    @NotNull(message=NOT_NULL_RESERVATION_DATE)
    @JsonProperty("reservationDate")
    private Instant reservationDate;

    private Boolean isReceived;

    @Valid
    @NotEmpty
    @JsonProperty("products")
    public List<ProductViewDto> products;
}
