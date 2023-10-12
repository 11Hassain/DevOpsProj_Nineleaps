package com.example.devopsproj.dto.requestdto;

import lombok.*;

/**
 * The OTPRequestDTO class represents a data transfer object for one-time password (OTP) verification.
 * It contains details such as the phone number, email address, and the OTP for verification.
 *
 * @version 2.0
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OTPRequestDTO {

    private String phone;
    private String email;
    private String otp;
}
