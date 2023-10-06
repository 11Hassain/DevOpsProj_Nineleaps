package com.example.devopsproj.otp.otpdto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SmsPojoTest {

    @Test
    void testGetterSetter() {
        SmsPojo smsPojo = new SmsPojo();
        String phoneNumber = "+1234567890";

        smsPojo.setPhoneNumber(phoneNumber);

        assertEquals(phoneNumber, smsPojo.getPhoneNumber());
    }

    @Test
    void testDefaultValues() {
        SmsPojo smsPojo = new SmsPojo();

        assertNull(smsPojo.getPhoneNumber());
    }
}
