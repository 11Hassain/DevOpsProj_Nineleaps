package com.example.devopsproj.dto.requestdto;

import lombok.*;
/**
 * Data Transfer Object (DTO) for representing an OTP (One-Time Password) request.
 */
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Setter
@Getter
public class OTPRequestDTO {

    /**
     * The phone number associated with the OTP request.
     */
    private String phone;

    /**
     * The email address associated with the OTP request.
     */
    private String email;

    /**
     * The OTP (One-Time Password) value.
     */
    private String otp;
}
