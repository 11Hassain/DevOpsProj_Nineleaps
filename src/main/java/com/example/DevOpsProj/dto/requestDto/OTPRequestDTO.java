package com.example.DevOpsProj.dto.requestDto;

import lombok.Data;

@Data
public class OTPRequestDTO {

    private String phone;
    private String email;
    private String otp;
}
