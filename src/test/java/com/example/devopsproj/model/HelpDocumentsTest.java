package com.example.devopsproj.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class HelpDocumentsTest {

    @Test
    void testHelpDocumentsGettersAndSetters() {
        HelpDocuments helpDocuments = new HelpDocuments();

        helpDocuments.setHelpDocumentId(1L);
        helpDocuments.setData(new byte[]{1, 2, 3});
        helpDocuments.setFileName("sample.pdf");
        helpDocuments.setCategory("General");
        helpDocuments.setFileExtension("pdf");

        assertEquals(1L, helpDocuments.getHelpDocumentId());
        assertArrayEquals(new byte[]{1, 2, 3}, helpDocuments.getData());
        assertEquals("sample.pdf", helpDocuments.getFileName());
        assertEquals("General", helpDocuments.getCategory());
        assertEquals("pdf", helpDocuments.getFileExtension());
    }

    @Test
    void testHelpDocumentsProjectAssociation() {
        HelpDocuments helpDocuments = new HelpDocuments();

        Project project = new Project();
        project.setProjectId(1L);
        project.setProjectName("Sample Project");

        HelpDocuments helpDocumentsWithProject = new HelpDocuments();
        helpDocumentsWithProject.setProject(project);

        assertEquals(project, helpDocumentsWithProject.getProject());
    }
}
