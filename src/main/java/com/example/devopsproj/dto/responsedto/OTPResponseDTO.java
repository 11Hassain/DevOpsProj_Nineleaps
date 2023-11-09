package com.example.devopsproj.dto.responsedto;

import com.example.devopsproj.commons.enumerations.OTPStatus;
import lombok.*;


/**
 * Data Transfer Object (DTO) for OTP response.
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class OTPResponseDTO {
    /**
     * The status of the OTP request (DELIVERED or FAILED).
     */
    private OTPStatus otpStatus;

    /**
     * A message associated with the OTP response.
     */
    private String message;
}
