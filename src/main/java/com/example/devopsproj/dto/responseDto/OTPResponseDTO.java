package com.example.devopsproj.dto.responseDto;

import com.example.devopsproj.commons.enumerations.OTPStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OTPResponseDTO {

    private OTPStatus otpStatus;
    private String message;
}
