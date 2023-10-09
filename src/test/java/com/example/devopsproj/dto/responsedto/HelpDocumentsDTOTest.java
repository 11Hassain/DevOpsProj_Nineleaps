package com.example.devopsproj.dto.responsedto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class HelpDocumentsDTOTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void testValidHelpDocumentsDTO() {
        // Arrange
        Long helpDocumentId = 1L;
        String fileName = "document.txt";

        // Act
        HelpDocumentsDTO dto = new HelpDocumentsDTO();
        dto.setHelpDocumentId(helpDocumentId);
        dto.setFileName(fileName);

        // Assert
        assertEquals(helpDocumentId, dto.getHelpDocumentId());
        assertEquals(fileName, dto.getFileName());
    }

    @Test
    void testNoArgConstructor() {
        HelpDocumentsDTO dto = new HelpDocumentsDTO();

        assertNotNull(dto);

        assertNull(dto.getHelpDocumentId());
        assertNull(dto.getFileName());
    }

    @Test
    void testFileNameMaxLength() {
        String fileName = "ThisIsAFileNameThatExceedsFiftyCharactersAndShouldCauseAValidationFailure.txt";
        HelpDocumentsDTO dto = new HelpDocumentsDTO(null, fileName);

        Set<ConstraintViolation<HelpDocumentsDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("File name should not exceed 50 characters", violations.iterator().next().getMessage());
    }

    @Test
    void testHelpDocumentIdPositive() {
        Long helpDocumentId = -1L;
        HelpDocumentsDTO dto = new HelpDocumentsDTO(helpDocumentId, null);

        Set<ConstraintViolation<HelpDocumentsDTO>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("helpDocumentId should be a positive numer", violations.iterator().next().getMessage());
    }

    @Test
    void testToString() {
        Long helpDocumentId = 2L;
        String fileName = "document.pdf";

        HelpDocumentsDTO dto = new HelpDocumentsDTO(helpDocumentId, fileName);

        String expectedToString = "HelpDocumentsDTO(helpDocumentId=2, fileName=document.pdf)";
        assertEquals(expectedToString, dto.toString());
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


}
