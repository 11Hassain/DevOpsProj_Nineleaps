package com.example.devopsproj.dto.responsedto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TempOTPTest {

    @Test
    void testSetAndGetOtp() {
        // Arrange
        TempOTP tempOTP = new TempOTP();
        int otpValue = 123456;

        // Act
        tempOTP.setOtp(otpValue);
        int retrievedOtp = tempOTP.getOtp();

        // Assert
        assertEquals(otpValue, retrievedOtp);
    }

    @Test
    void testSetOtpMultipleTimes() {
        // Arrange
        TempOTP tempOTP = new TempOTP();
        int otpValue1 = 654123;
        int otpValue2 = 654390;

        // Act
        tempOTP.setOtp(otpValue1);
        tempOTP.setOtp(otpValue2);

        // Assert
        assertEquals(otpValue2, tempOTP.getOtp());
    }

    @Test
    void testSetOtpNegative() {
        // Arrange
        TempOTP tempOTP = new TempOTP();
        int otpValue = -123456;

        // Act
        tempOTP.setOtp(otpValue);

        // Assert
        assertEquals(otpValue, tempOTP.getOtp());
    }

    @Test
    void testSetOtpZero() {
        // Arrange
        TempOTP tempOTP = new TempOTP();
        int otpValue = 0;

        // Act
        tempOTP.setOtp(otpValue);

        // Assert
        assertEquals(otpValue, tempOTP.getOtp());
    }
}
