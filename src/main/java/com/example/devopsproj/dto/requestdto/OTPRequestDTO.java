package com.example.devopsproj.dto.requestdto;

import lombok.Data;

@Data
public class OTPRequestDTO {

    private String phone;
    private String email;
    private String otp;
}
