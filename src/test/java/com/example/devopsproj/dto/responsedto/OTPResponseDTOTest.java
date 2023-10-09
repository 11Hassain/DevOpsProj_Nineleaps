package com.example.devopsproj.dto.responsedto;

import com.example.devopsproj.commons.enumerations.OTPStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OTPResponseDTOTest {

    @Test
    void testGetSetOtpStatus() {
        // Arrange
        OTPStatus otpStatus = OTPStatus.DELIVERED;

        // Act
        OTPResponseDTO otpResponseDTO = new OTPResponseDTO();
        otpResponseDTO.setOtpStatus(otpStatus);

        // Assert
        assertEquals(otpStatus, otpResponseDTO.getOtpStatus());
    }

    @Test
    void testGetSetMessage() {
        // Arrange
        String message = "OTP sent successfully";

        // Act
        OTPResponseDTO otpResponseDTO = new OTPResponseDTO();
        otpResponseDTO.setMessage(message);

        // Assert
        assertEquals(message, otpResponseDTO.getMessage());
    }


    @Test
    void testGetterSetter() {
        OTPResponseDTO otpResponseDTO = new OTPResponseDTO();

        otpResponseDTO.setOtpStatus(OTPStatus.FAILED);
        otpResponseDTO.setMessage("OTP verification failed");

        assertEquals(OTPStatus.FAILED, otpResponseDTO.getOtpStatus());
        assertEquals("OTP verification failed", otpResponseDTO.getMessage());
    }

    @Test
    void testNoArgConstructor() {
        // Act
        OTPResponseDTO otpResponseDTO = new OTPResponseDTO();

        // Assert
        assertNull(otpResponseDTO.getOtpStatus());
        assertNull(otpResponseDTO.getMessage());
    }

    @Test
    void testAllArgsConstructor() {
        // Arrange
        OTPStatus otpStatus = OTPStatus.FAILED;
        String message = "Invalid OTP";

        // Act
        OTPResponseDTO otpResponseDTO = new OTPResponseDTO(otpStatus, message);

        // Assert
        assertEquals(otpStatus, otpResponseDTO.getOtpStatus());
        assertEquals(message, otpResponseDTO.getMessage());
    }
}
