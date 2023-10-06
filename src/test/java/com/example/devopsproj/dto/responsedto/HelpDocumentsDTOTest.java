package com.example.devopsproj.dto.responsedto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HelpDocumentsDTOTest {

    @Test
    void testNoArgsConstructor() {
        HelpDocumentsDTO helpDocumentsDTO = new HelpDocumentsDTO();

        assertNotNull(helpDocumentsDTO);

        assertNull(helpDocumentsDTO.getHelpDocumentId());
        assertNull(helpDocumentsDTO.getFileName());
    }

    @Test
    void testAllArgsConstructor() {
        HelpDocumentsDTO helpDocumentsDTO = new HelpDocumentsDTO(1L, "Example.pdf");

        assertEquals(1L, helpDocumentsDTO.getHelpDocumentId());
        assertEquals("Example.pdf", helpDocumentsDTO.getFileName());
    }

    @Test
    void testSetterGetter() {
        HelpDocumentsDTO helpDocumentsDTO = new HelpDocumentsDTO();

        helpDocumentsDTO.setHelpDocumentId(2L);
        helpDocumentsDTO.setFileName("NewFile.doc");

        assertEquals(2L, helpDocumentsDTO.getHelpDocumentId());
        assertEquals("NewFile.doc", helpDocumentsDTO.getFileName());
    }

    @Test
    void testToString() {
        HelpDocumentsDTO helpDocumentsDTO = new HelpDocumentsDTO(1L, "Example.pdf");

        String expectedToString = "HelpDocumentsDTO(helpDocumentId=1, fileName=Example.pdf)";
        assertEquals(expectedToString, helpDocumentsDTO.toString());
    }
}
