package com.example.devopsproj.dto.responsedto;

import com.example.devopsproj.commons.enumerations.OTPStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OTPResponseDTOTest {

    @Test
    void testGetterSetter() {
        OTPResponseDTO otpResponseDTO = new OTPResponseDTO();

        otpResponseDTO.setOtpStatus(OTPStatus.FAILED);
        otpResponseDTO.setMessage("OTP verification failed");

        assertEquals(OTPStatus.FAILED, otpResponseDTO.getOtpStatus());
        assertEquals("OTP verification failed", otpResponseDTO.getMessage());
    }

    @Test
    void testNoArgsConstructor() {
        OTPResponseDTO otpResponseDTO = new OTPResponseDTO();

        assertNull(otpResponseDTO.getOtpStatus());
        assertNull(otpResponseDTO.getMessage());
    }

    @Test
    void testAllArgsConstructor() {
        OTPStatus status = OTPStatus.DELIVERED;
        String message = "This is a test message";
        OTPResponseDTO otpResponseDTO = new OTPResponseDTO(status, message);

        assertEquals(status, otpResponseDTO.getOtpStatus());
        assertEquals(message, otpResponseDTO.getMessage());
    }
}
