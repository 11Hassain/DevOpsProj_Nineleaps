package com.example.devopsproj.dto.requestdto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Setter
@Getter
public class OTPRequestDTO {

    private String phone;
    private String email;
    private String otp;
}
