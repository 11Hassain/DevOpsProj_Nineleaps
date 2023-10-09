package com.example.devopsproj.dto.responsedto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class FigmaScreenshotDTOTest {

    @Test
    void testValidFigmaScreenshotDTO() {
        // Arrange
        String user = "John Doe";
        String screenshotImageURL = "https://example.com/image.png";

        // Act
        FigmaScreenshotDTO dto = new FigmaScreenshotDTO();
        dto.setUser(user);
        dto.setScreenshotImageURL(screenshotImageURL);

        // Assert
        assertEquals(user, dto.getUser());
        assertEquals(screenshotImageURL, dto.getScreenshotImageURL());
    }


    @Test
    void testNoArgsConstructor() {
        FigmaScreenshotDTO figmaScreenshotDTO = new FigmaScreenshotDTO();

        assertNotNull(figmaScreenshotDTO);

        assertNull(figmaScreenshotDTO.getUser());
        assertNull(figmaScreenshotDTO.getScreenshotImageURL());
    }

    @Test
    void testAllArgsConstructor() {
        FigmaScreenshotDTO figmaScreenshotDTO = new FigmaScreenshotDTO("John Doe", "https://example.com/screenshot.jpg");

        assertEquals("John Doe", figmaScreenshotDTO.getUser());
        assertEquals("https://example.com/screenshot.jpg", figmaScreenshotDTO.getScreenshotImageURL());
    }

    @Test
    void testSetterGetter() {
        FigmaScreenshotDTO figmaScreenshotDTO = new FigmaScreenshotDTO();

        figmaScreenshotDTO.setUser("Alice");
        figmaScreenshotDTO.setScreenshotImageURL("https://example.com/image.png");

        assertEquals("Alice", figmaScreenshotDTO.getUser());
        assertEquals("https://example.com/image.png", figmaScreenshotDTO.getScreenshotImageURL());
    }

    @Test
    void testToString() {
        FigmaScreenshotDTO figmaScreenshotDTO = new FigmaScreenshotDTO("Bob", "https://example.com/pic.jpg");

        String expectedToString = "FigmaScreenshotDTO(user=Bob, screenshotImageURL=https://example.com/pic.jpg)";
        assertEquals(expectedToString, figmaScreenshotDTO.toString());
    }
}

