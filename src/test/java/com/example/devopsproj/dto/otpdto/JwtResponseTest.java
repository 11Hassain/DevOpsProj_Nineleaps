package com.example.devopsproj.dto.otpdto;

import com.example.devopsproj.dto.otpdto.JwtResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtResponseTest {

    @Test
    void testNoArgsConstructor() {
        JwtResponse jwtResponse = new JwtResponse();

        assertNull(jwtResponse.getAccessToken());
        assertNull(jwtResponse.getRefreshToken());
        assertNull(jwtResponse.getErrorMessage());
    }

    @Test
    void testAllArgsConstructor() {
        String accessToken = "access_token";
        String refreshToken = "refresh_token";
        String errorMessage = "error_message";

        JwtResponse jwtResponse = new JwtResponse(accessToken, refreshToken, errorMessage);

        assertEquals(accessToken, jwtResponse.getAccessToken());
        assertEquals(refreshToken, jwtResponse.getRefreshToken());
        assertEquals(errorMessage, jwtResponse.getErrorMessage());
    }

    @Test
    void testGetterSetter() {
        JwtResponse jwtResponse = new JwtResponse();
        String accessToken = "new_access_token";
        String refreshToken = "new_refresh_token";
        String errorMessage = "new_error_message";

        jwtResponse.setAccessToken(accessToken);
        jwtResponse.setRefreshToken(refreshToken);
        jwtResponse.setErrorMessage(errorMessage);

        assertEquals(accessToken, jwtResponse.getAccessToken());
        assertEquals(refreshToken, jwtResponse.getRefreshToken());
        assertEquals(errorMessage, jwtResponse.getErrorMessage());
    }
}
