package com.example.devopsproj.model;

import com.example.devopsproj.model.HelpDocuments;
import com.example.devopsproj.model.Project;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class HelpDocumentsTest {

    @Test
    public void testAllArgsConstructor() {
        Long helpDocumentId = 1L;
        byte[] data = new byte[]{1, 2, 3};
        String fileName = "document.pdf";
        String category = "Documentation";
        String fileExtension = "pdf";
        Project project = new Project(); // You can create a Project instance here

        HelpDocuments helpDocuments = new HelpDocuments(
                helpDocumentId, data, fileName, category, fileExtension, project);

        assertEquals(helpDocumentId, helpDocuments.getHelpDocumentId());
        assertArrayEquals(data, helpDocuments.getData());
        assertEquals(fileName, helpDocuments.getFileName());
        assertEquals(category, helpDocuments.getCategory());
        assertEquals(fileExtension, helpDocuments.getFileExtension());
        assertEquals(project, helpDocuments.getProject());
    }

    @Test
    public void testNoArgsConstructor() {
        HelpDocuments helpDocuments = new HelpDocuments();

        assertNull(helpDocuments.getHelpDocumentId());
        assertNull(helpDocuments.getData());
        assertNull(helpDocuments.getFileName());
        assertNull(helpDocuments.getCategory());
        assertNull(helpDocuments.getFileExtension());
        assertNull(helpDocuments.getProject());
    }

    @Test
    public void testGetterSetter() {
        HelpDocuments helpDocuments = new HelpDocuments();

        Long helpDocumentId = 1L;
        byte[] data = new byte[]{1, 2, 3};
        String fileName = "document.pdf";
        String category = "Documentation";
        String fileExtension = "pdf";
        Project project = new Project(); // You can create a Project instance here

        helpDocuments.setHelpDocumentId(helpDocumentId);
        helpDocuments.setData(data);
        helpDocuments.setFileName(fileName);
        helpDocuments.setCategory(category);
        helpDocuments.setFileExtension(fileExtension);
        helpDocuments.setProject(project);

        assertEquals(helpDocumentId, helpDocuments.getHelpDocumentId());
        assertArrayEquals(data, helpDocuments.getData());
        assertEquals(fileName, helpDocuments.getFileName());
        assertEquals(category, helpDocuments.getCategory());
        assertEquals(fileExtension, helpDocuments.getFileExtension());
        assertEquals(project, helpDocuments.getProject());
    }

    @Test
    void testHelpDocumentsWithProject() {
        HelpDocuments helpDocuments = new HelpDocuments();

        Project project = new Project();
        project.setProjectId(1L);
        project.setProjectName("Sample Project");

        HelpDocuments helpDocumentsWithProject = new HelpDocuments();
        helpDocumentsWithProject.setProject(project);

        assertEquals(project, helpDocumentsWithProject.getProject());
    }
}
