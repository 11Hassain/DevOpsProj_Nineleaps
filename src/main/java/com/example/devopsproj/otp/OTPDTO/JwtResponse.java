package com.example.devopsproj.otp.OTPDTO;

import lombok.*;

@Data
@Getter@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JwtResponse {

    private String accessToken;
    private String refreshToken;
    private String errorMessage;

}