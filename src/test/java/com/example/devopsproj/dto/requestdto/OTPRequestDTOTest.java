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

        // Test equals and hashCode
        OTPRequestDTO anotherDTO = new OTPRequestDTO("1234567890", "test@example.com", "1234");
        assertEquals(otpRequestDTO, anotherDTO);
        assertEquals(otpRequestDTO.hashCode(), anotherDTO.hashCode());

        // Test toString
        String expectedToString = "OTPRequestDTO(phone=1234567890, email=test@example.com, otp=1234)";
        assertEquals(expectedToString, otpRequestDTO.toString());
    }

    @Test
    void testEqualsAndHashCode() {
        OTPRequestDTO dto1 = new OTPRequestDTO("1234567890", "test@example.com", "1234");
        OTPRequestDTO dto2 = new OTPRequestDTO("1234567890", "test@example.com", "1234");
        OTPRequestDTO dto3 = new OTPRequestDTO("9876543210", "another@example.com", "5678");

        // Test equals
        assertEquals(dto1, dto2);
        assertNotEquals(dto1, dto3);

        // Test hashCode
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotEquals(dto1.hashCode(), dto3.hashCode());
    }
}
