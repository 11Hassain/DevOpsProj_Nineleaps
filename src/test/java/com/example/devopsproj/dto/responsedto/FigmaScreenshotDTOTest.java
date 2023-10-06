package com.example.devopsproj.dto.responsedto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FigmaScreenshotDTOTest {

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
