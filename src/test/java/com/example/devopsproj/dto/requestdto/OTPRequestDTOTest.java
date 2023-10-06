package com.example.devopsproj.dto.requestdto;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

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
}
