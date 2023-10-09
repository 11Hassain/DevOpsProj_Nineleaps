package com.example.devopsproj.dto.responsedto;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.*;

class StoreOTPTest {

    @Test
    void testSetAndGetOtp() {
        // Arrange
        int otpValue = 123456;

        // Act
        StoreOTP.setOtp(otpValue);
        int retrievedOtp = StoreOTP.getOtp();

        // Assert
        assertEquals(otpValue, retrievedOtp);
    }




    @Test
    void testSetOtpMultipleTimes() {
        int otpValue1 = 654123;
        int otpValue2 = 654390;

        StoreOTP.setOtp(otpValue1);
        StoreOTP.setOtp(otpValue2);

        assertEquals(otpValue2, StoreOTP.getOtp());
    }

    @Test
    void testSetOtpNegative() {
        int otpValue = -123456;

        StoreOTP.setOtp(otpValue);

        assertEquals(otpValue, StoreOTP.getOtp());
    }

    @Test
    void testSetOtpZero() {
        int otpValue = 0;

        StoreOTP.setOtp(otpValue);

        assertEquals(otpValue, StoreOTP.getOtp());
    }


    @Test
    void testPrivateConstructor() {
        // Use reflection to access the private constructor
        Constructor<StoreOTP> constructor = null;
        try {
            constructor = StoreOTP.class.getDeclaredConstructor();
            constructor.setAccessible(true);

            // Attempt to create an instance, which should throw UnsupportedOperationException
            Constructor<StoreOTP> finalConstructor = constructor;
            assertThrows(InvocationTargetException.class, finalConstructor::newInstance);

        } catch (NoSuchMethodException e) {
            fail("Private constructor not found.");
        } finally {
            if (constructor != null) {
                constructor.setAccessible(false);
            }
        }
    }
}
