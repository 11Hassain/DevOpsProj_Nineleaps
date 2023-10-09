package com.example.devopsproj.service.implementations;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


class SmsServiceTest {

    private SmsService smsService;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        smsService = new SmsService();
        smsService.setPhoneNumber("+1234567890"); // Set a sample phone number
    }

    @Test
    void testSetPhoneNumber() {
        // Arrange
        String newPhoneNumber = "+1234567890";

        // Act
        smsService.setPhoneNumber(newPhoneNumber);

        // Assert
        assertEquals(newPhoneNumber, smsService.getPhoneNumber());
    }
    @Test
    void testGetTimeStamp() {
        // Arrange
        smsService = new SmsService();

        // Act
        String timeStamp = smsService.getTimeStamp();

        // Assert
        // Define the expected timestamp pattern (e.g., "yyyy-MM-dd HH:mm:ss")
        String expectedPattern = "yyyy-MM-dd HH:mm:ss";

        // Use DateTimeFormatter to parse the timestamp with the expected pattern
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(expectedPattern);
        try {
            LocalDateTime.parse(timeStamp, formatter);
            assertTrue(true); // If parsing succeeds, the test passes
        } catch (Exception e) {
            assertTrue(false); // If parsing fails, the test fails
        }
    }
}