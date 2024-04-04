package com.preorder.global.error.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Setter(AccessLevel.PROTECTED)
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponseDto {

    private String ErrorCode;
    private String Message;
}
