package com.example.devopsproj.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class FigmaTest {

    @InjectMocks
    private Figma figma;
    @Mock
    private Project project;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFigmaGettersAndSetters() {
        Figma figma = new Figma();

        figma.setFigmaId(1L);
        figma.setFigmaURL("https://www.example.com/figma");
        figma.setUser("john_doe");

        assertEquals(1L, figma.getFigmaId());
        assertEquals("https://www.example.com/figma", figma.getFigmaURL());
        assertEquals("john_doe", figma.getUser());
    }

    @Test
    void testFigmaScreenshotImages() {
        Figma figma = new Figma();

        Map<String, String> screenshotImages = new HashMap<>();
        screenshotImages.put("user1", "screenshot1.jpg");
        screenshotImages.put("user2", "screenshot2.jpg");

        figma.setScreenshotImagesByUser(screenshotImages);

        assertEquals(screenshotImages, figma.getScreenshotImagesByUser());
    }

    @Test
    void testFigmaProjectAssociation() {
        Figma figma = new Figma();

        Project project = new Project();
        project.setProjectId(1L);
        project.setProjectName("Sample Project");

        figma.setProject(project);

        assertEquals(project, figma.getProject());
    }

}
