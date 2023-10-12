package com.example.devopsproj.dto.responsedto;

import com.example.devopsproj.commons.enumerations.OTPStatus;
import lombok.*;

/**
 * The OTPResponseDTO class represents a data transfer object for OTP (One-Time Password) response information.
 * It includes details such as the OTP status and an optional message.
 *
 * @version 2.0
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OTPResponseDTO {

    private OTPStatus otpStatus;
    private String message;
}
