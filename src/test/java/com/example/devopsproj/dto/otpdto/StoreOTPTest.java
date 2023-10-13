package com.example.devopsproj.dto.otpdto;

import com.example.devopsproj.dto.otpdto.StoreOTP;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.*;

class StoreOTPTest {

    @Test
    void testSetOtp() {
        int otpValue = 123456;

        StoreOTP.setOtp(otpValue);

        assertEquals(otpValue, StoreOTP.getOtp());
    }

    @Test
    void testSetOtpMultipleTimes() {
        int otpValue1 = 123456;
        int otpValue2 = 654321;

        StoreOTP.setOtp(otpValue1);
        StoreOTP.setOtp(otpValue2);

        assertEquals(otpValue2, StoreOTP.getOtp());
    }

    @Test
    void testSetOtpNegativeValue() {
        int otpValue = -123456;

        StoreOTP.setOtp(otpValue);

        assertEquals(otpValue, StoreOTP.getOtp());
    }

    @Test
    void testSetOtpZeroValue() {
        int otpValue = 0;

        StoreOTP.setOtp(otpValue);

        assertEquals(otpValue, StoreOTP.getOtp());
    }

    @Test
    void testGetOtpDefault() {
        assertEquals(0, StoreOTP.getOtp());
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
