package com.example.devopsproj.dto.responsedto;

import com.example.devopsproj.commons.enumerations.OTPStatus;
import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter

public class OTPResponseDTO {

    private OTPStatus otpStatus;
    private String message;
}