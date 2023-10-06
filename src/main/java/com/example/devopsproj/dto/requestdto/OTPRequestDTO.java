package com.example.devopsproj.dto.requestdto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OTPRequestDTO {

    private String phone;
    private String email;
    private String otp;
}
