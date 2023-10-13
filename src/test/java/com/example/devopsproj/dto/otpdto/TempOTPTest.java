package com.example.devopsproj.dto.otpdto;

import com.example.devopsproj.dto.otpdto.TempOTP;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TempOTPTest {

    @Test
    void testGettersAndSetters() {
        // Create an instance of TempOTP
        TempOTP tempOTP = new TempOTP();

        // Set the OTP value using the setter
        tempOTP.setOtp(123456);

        // Check if the getter returns the correct OTP value
        assertEquals(123456, tempOTP.getOtp());

        // Set a different OTP value
        tempOTP.setOtp(789012);

        // Check if the getter returns the updated OTP value
        assertEquals(789012, tempOTP.getOtp());
    }
}
