package com.example.devopsproj.dto.responsedto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FigmaDTOTest {

    @Test
    void testConstructorAndGetters() {
        String figmaURL = "https://example.com/figma";
        FigmaDTO figmaDTO = new FigmaDTO(figmaURL);

        assertEquals(figmaURL, figmaDTO.getFigmaURL());
    }

    @Test
    void testDefaultConstructor() {
        FigmaDTO figmaDTO = new FigmaDTO();

        assertNull(figmaDTO.getProjectDTO());
        assertNull(figmaDTO.getFigmaURL());
        assertNull(figmaDTO.getScreenshotImage());
        assertNull(figmaDTO.getUser());
        assertNull(figmaDTO.getFigmaId());
    }

    @Test
    void testSetters() {
        FigmaDTO figmaDTO = new FigmaDTO();
        String figmaURL = "https://newexample.com/figma";
        figmaDTO.setFigmaURL(figmaURL);

        assertEquals(figmaURL, figmaDTO.getFigmaURL());
    }

    @Test
    void testConstructorWithProjectDTO() {
        String figmaURL = "https://example.com/figma";
        ProjectDTO projectDTO = new ProjectDTO();
        FigmaDTO figmaDTO = new FigmaDTO(projectDTO, figmaURL);

        assertEquals(projectDTO, figmaDTO.getProjectDTO());
        assertEquals(figmaURL, figmaDTO.getFigmaURL());
    }

    @Test
    void testConstructorWithFigmaId() {
        String figmaURL = "https://example.com/figma";
        ProjectDTO projectDTO = new ProjectDTO();
        Long figmaId = 1L;
        FigmaDTO figmaDTO = new FigmaDTO(figmaId, projectDTO, figmaURL);

        assertEquals(projectDTO, figmaDTO.getProjectDTO());
        assertEquals(figmaURL, figmaDTO.getFigmaURL());
        assertEquals(figmaId, figmaDTO.getFigmaId());
    }

    @Test
    void testConstructorWithScreenshotImageAndUser() {
        String screenshotImage = "screenshot.png";
        String user = "User123";
        FigmaDTO figmaDTO = new FigmaDTO(screenshotImage, user);

        assertEquals(screenshotImage, figmaDTO.getScreenshotImage());
        assertEquals(user, figmaDTO.getUser());
    }

    @Test
    void testPositiveFigmaId() {
        Long figmaId = 1L;
        FigmaDTO figmaDTO = new FigmaDTO(figmaId, null, null);

        assertEquals(figmaId, figmaDTO.getFigmaId());
    }

    @Test
    void testPositiveFigmaIdWithZero() {
        Long figmaId = 0L;
        FigmaDTO figmaDTO = new FigmaDTO(figmaId, null, null);

        assertEquals(figmaId, figmaDTO.getFigmaId());
    }
    @Test
    void testToString() {
        // Arrange
        FigmaDTO figmaDTO = new FigmaDTO();

        // Act
        String toStringResult = figmaDTO.toString();

        // Assert
        assertNotNull(toStringResult);
    }


}
