package com.example.devopsproj.dto.requestdto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class OTPRequestDTOTest {

    @Test
    void testConstructorAndGetters() {
        String phone = "1234567890";
        String email = "test@example.com";
        String otp = "123456";

        OTPRequestDTO otpRequestDTO = new OTPRequestDTO(phone, email, otp);

        assertEquals(phone, otpRequestDTO.getPhone());
        assertEquals(email, otpRequestDTO.getEmail());
        assertEquals(otp, otpRequestDTO.getOtp());
    }
    @Test
    void testDefaultConstructor() {
        OTPRequestDTO otpRequestDTO = new OTPRequestDTO();

        assertNull(otpRequestDTO.getPhone());
        assertNull(otpRequestDTO.getEmail());
        assertNull(otpRequestDTO.getOtp());
    }

    @Test
    void testSetters() {
        OTPRequestDTO otpRequestDTO = new OTPRequestDTO();
        String phone = "9876543210";
        String email = "another@example.com";
        String otp = "654321";

        otpRequestDTO.setPhone(phone);
        otpRequestDTO.setEmail(email);
        otpRequestDTO.setOtp(otp);

        assertEquals(phone, otpRequestDTO.getPhone());
        assertEquals(email, otpRequestDTO.getEmail());
        assertEquals(otp, otpRequestDTO.getOtp());
    }

    @Test
    void testGeneratedMethods() {
        // Create an instance of OTPRequestDTO
        OTPRequestDTO otpRequestDTO = new OTPRequestDTO();
        otpRequestDTO.setPhone("1234567890");
        otpRequestDTO.setEmail("test@example.com");
        otpRequestDTO.setOtp("1234");

        // Test getters
        assertEquals("1234567890", otpRequestDTO.getPhone());
        assertEquals("test@example.com", otpRequestDTO.getEmail());
        assertEquals("1234", otpRequestDTO.getOtp());

    }
    @Test
    void testConstructorWithEmptyValues() {
        OTPRequestDTO otpRequestDTO = new OTPRequestDTO("", "", "");
        assertEquals("", otpRequestDTO.getPhone());
        assertEquals("", otpRequestDTO.getEmail());
        assertEquals("", otpRequestDTO.getOtp());
    }

    @Test
    void testConstructorWithNullValues() {
        OTPRequestDTO otpRequestDTO = new OTPRequestDTO(null, null, null);
        assertNull(otpRequestDTO.getPhone());
        assertNull(otpRequestDTO.getEmail());
        assertNull(otpRequestDTO.getOtp());
    }

    @Test
    void testConstructorWithLongValues() {
        // Create a very long phone number, email, and OTP
        String longPhone = "1".repeat(1000);
        String longEmail = "a".repeat(1000) + "@example.com";
        String longOtp = "9".repeat(1000);

        OTPRequestDTO otpRequestDTO = new OTPRequestDTO(longPhone, longEmail, longOtp);

        assertEquals(longPhone, otpRequestDTO.getPhone());
        assertEquals(longEmail, otpRequestDTO.getEmail());
        assertEquals(longOtp, otpRequestDTO.getOtp());
    }
}

