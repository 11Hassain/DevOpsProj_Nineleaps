package com.example.devopsproj.dto.responsedto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SmsPojoTest {

    @Test
    void testValidSmsPojo() {
        // Arrange
        String phoneNumber = "1234567890";

        // Act
        SmsPojo smsPojo = new SmsPojo();
        smsPojo.setPhoneNumber(phoneNumber);

        // Assert
        assertEquals(phoneNumber, smsPojo.getPhoneNumber());
    }

    @Test
    void testNoArgsConstructor() {
        // Act
        SmsPojo smsPojo = new SmsPojo();

        // Assert
        assertNull(smsPojo.getPhoneNumber());
    }

    @Test
    void testAllArgsConstructor() {
        // Arrange
        String phoneNumber = "9876543210";

        // Act
        SmsPojo smsPojo = new SmsPojo(phoneNumber);

        // Assert
        assertEquals(phoneNumber, smsPojo.getPhoneNumber());
    }

    @Test
    void testSetterGetter() {
        // Arrange
        String phoneNumber = "5555555555";

        // Act
        SmsPojo smsPojo = new SmsPojo();
        smsPojo.setPhoneNumber(phoneNumber);

        // Assert
        assertEquals(phoneNumber, smsPojo.getPhoneNumber());
    }

    @Test
    void testToString() {
        // Arrange
        String phoneNumber = "1112223333";

        // Act
        SmsPojo smsPojo = new SmsPojo(phoneNumber);

        // Assert
        String expectedToString = "SmsPojo(phoneNumber=1112223333)";
        assertEquals(expectedToString, smsPojo.toString());
    }
}
