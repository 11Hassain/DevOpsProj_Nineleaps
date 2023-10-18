package com.example.devopsproj.model;

import com.example.devopsproj.model.Figma;
import com.example.devopsproj.model.Project;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

 class FigmaTest {

    @Test
     void testFigmaInitialization() {
        Figma figma = new Figma();

        assertNull(figma.getFigmaId());
        assertNull(figma.getFigmaURL());
        assertNull(figma.getUser());
        assertNull(figma.getScreenshotImagesByUser());
        assertNull(figma.getProject());
    }

    @Test
     void testFigmaSetterGetter() {
        Long figmaId = 1L;
        String figmaURL = "https://example.com/figma";
        String user = "JohnDoe";
        Project project = new Project(); // You can create a Project instance here

        Map<String, String> screenshotImagesByUser = new HashMap<>();
        screenshotImagesByUser.put("User1", "Image1.png");
        screenshotImagesByUser.put("User2", "Image2.png");

        Figma figma = new Figma();
        figma.setFigmaId(figmaId);
        figma.setFigmaURL(figmaURL);
        figma.setUser(user);
        figma.setScreenshotImagesByUser(screenshotImagesByUser);
        figma.setProject(project);

        assertEquals(figmaId, figma.getFigmaId());
        assertEquals(figmaURL, figma.getFigmaURL());
        assertEquals(user, figma.getUser());
        assertEquals(screenshotImagesByUser, figma.getScreenshotImagesByUser());
        assertEquals(project, figma.getProject());
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
    void testFigmaWithProject() {
        Figma figma = new Figma();

        Project project = new Project();
        project.setProjectId(1L);
        project.setProjectName("Sample Project");

        figma.setProject(project);

        assertEquals(project, figma.getProject());
    }
    @Test
     void testAllArgsConstructor() {
        Long figmaId = 1L;
        String figmaURL = "https://example.com/figma";
        String user = "JohnDoe";
        Project project = new Project(); // You can create a Project instance here

        Map<String, String> screenshotImagesByUser = new HashMap<>();
        screenshotImagesByUser.put("User1", "Image1.png");
        screenshotImagesByUser.put("User2", "Image2.png");

        Figma figma = new Figma(figmaId, figmaURL, user, screenshotImagesByUser, project);

        assertEquals(figmaId, figma.getFigmaId());
        assertEquals(figmaURL, figma.getFigmaURL());
        assertEquals(user, figma.getUser());
        assertEquals(screenshotImagesByUser, figma.getScreenshotImagesByUser());
        assertEquals(project, figma.getProject());
    }

    @Test
     void testNoArgsConstructor() {
        Figma figma = new Figma();

        assertNull(figma.getFigmaId());
        assertNull(figma.getFigmaURL());
        assertNull(figma.getUser());
        assertNull(figma.getScreenshotImagesByUser());
        assertNull(figma.getProject());
    }
}
