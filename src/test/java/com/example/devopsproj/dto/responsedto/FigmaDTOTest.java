package com.example.devopsproj.dto.responsedto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FigmaDTOTest {

    @Test
    void testNoArgsConstructor() {
        FigmaDTO figmaDTO = new FigmaDTO();

        assertNull(figmaDTO.getProjectDTO());
        assertNull(figmaDTO.getFigmaURL());
        assertNull(figmaDTO.getScreenshotImage());
        assertNull(figmaDTO.getUser());
        assertNull(figmaDTO.getFigmaId());
    }

    @Test
    void testGetterSetter() {
        FigmaDTO figmaDTO = new FigmaDTO();

        ProjectDTO projectDTO = new ProjectDTO(1L, "ProjectName");

        figmaDTO.setProjectDTO(projectDTO);
        figmaDTO.setFigmaURL("https://example.com/figma");
        figmaDTO.setScreenshotImage("screenshot.png");
        figmaDTO.setUser("user123");
        figmaDTO.setFigmaId(123L);

        assertEquals(projectDTO, figmaDTO.getProjectDTO());
        assertEquals("https://example.com/figma", figmaDTO.getFigmaURL());
        assertEquals("screenshot.png", figmaDTO.getScreenshotImage());
        assertEquals("user123", figmaDTO.getUser());
        assertEquals(123L, figmaDTO.getFigmaId());
    }
}
