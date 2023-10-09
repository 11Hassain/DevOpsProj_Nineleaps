package com.example.devopsproj.dto.responsedto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class GoogleDriveDTOTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void testValidGoogleDriveDTO() {
        // Arrange
        ProjectDTO projectDTO = new ProjectDTO();
        String driveLink = "https://drive.google.com/myfile";
        Long driveId = 1L;

        // Act
        GoogleDriveDTO dto = new GoogleDriveDTO();
        dto.setProjectDTO(projectDTO);
        dto.setDriveLink(driveLink);
        dto.setDriveId(driveId);

        // Assert
        assertEquals(projectDTO, dto.getProjectDTO());
        assertEquals(driveLink, dto.getDriveLink());
        assertEquals(driveId, dto.getDriveId());
    }

    @Test
    void testNoArgConstructor() {
        GoogleDriveDTO dto = new GoogleDriveDTO();

        assertNotNull(dto);

        assertNull(dto.getProjectDTO());
        assertNull(dto.getDriveLink());
        assertNull(dto.getDriveId());
    }

    @Test
    void testAllArgsConstructor() {
        ProjectDTO projectDTO = new ProjectDTO();
        GoogleDriveDTO googleDriveDTO = new GoogleDriveDTO(projectDTO, "DriveLink", 1L);

        assertEquals(projectDTO, googleDriveDTO.getProjectDTO());
        assertEquals("DriveLink", googleDriveDTO.getDriveLink());
        assertEquals(1L, googleDriveDTO.getDriveId());

    }

    @Test
    void testSetterGetter() {
        GoogleDriveDTO googleDriveDTO = new GoogleDriveDTO();

        ProjectDTO projectDTO = new ProjectDTO();
        googleDriveDTO.setProjectDTO(projectDTO);
        googleDriveDTO.setDriveLink("NewDriveLink");
        googleDriveDTO.setDriveId(2L);


        assertEquals(projectDTO, googleDriveDTO.getProjectDTO());
        assertEquals("NewDriveLink", googleDriveDTO.getDriveLink());
        assertEquals(2L, googleDriveDTO.getDriveId());

    }

    @Test
    void testCustomConstructor() {
        GoogleDriveDTO googleDriveDTO = new GoogleDriveDTO("CustomDriveLink");

        assertNull(googleDriveDTO.getProjectDTO());
        assertEquals("CustomDriveLink", googleDriveDTO.getDriveLink());
        assertNull(googleDriveDTO.getDriveId());

    }


    @Test
    void testToString() {
        ProjectDTO projectDTO = new ProjectDTO();
        String driveLink = "https://drive.google.com/myfile";
        Long driveId = 2L;

        GoogleDriveDTO dto = new GoogleDriveDTO(projectDTO, driveLink, driveId);

        String expectedToString = "GoogleDriveDTO(projectDTO=" + projectDTO.toString() + ", driveLink=https://drive.google.com/myfile, driveId=2)";
        assertEquals(expectedToString, dto.toString());
    }
}
