package com.example.devopsproj.dto.otpdto;

import lombok.*;

/**
 * The `JwtResponse` class represents the response containing JWT tokens and error messages.
 *
 * @version 2.0
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JwtResponse {

    private String accessToken;
    private String refreshToken;
    private String errorMessage;

}