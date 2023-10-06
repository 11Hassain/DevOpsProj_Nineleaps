package com.example.devopsproj.dto.responsedto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GoogleDriveDTOTest {

    @Test
    void testNoArgsConstructor() {
        GoogleDriveDTO googleDriveDTO = new GoogleDriveDTO();

        assertNotNull(googleDriveDTO);

        assertNull(googleDriveDTO.getProjectDTO());
        assertNull(googleDriveDTO.getDriveLink());
        assertNull(googleDriveDTO.getDriveId());
        assertNull(googleDriveDTO.getMessage());
    }
    @Test
    void testAllArgsConstructor() {
        ProjectDTO projectDTO = new ProjectDTO();
        GoogleDriveDTO googleDriveDTO = new GoogleDriveDTO(projectDTO, "DriveLink", 1L);

        assertEquals(projectDTO, googleDriveDTO.getProjectDTO());
        assertEquals("DriveLink", googleDriveDTO.getDriveLink());
        assertEquals(1L, googleDriveDTO.getDriveId());
        assertNull(googleDriveDTO.getMessage());
    }

    @Test
    void testSetterGetter() {
        GoogleDriveDTO googleDriveDTO = new GoogleDriveDTO();

        ProjectDTO projectDTO = new ProjectDTO();
        googleDriveDTO.setProjectDTO(projectDTO);
        googleDriveDTO.setDriveLink("NewDriveLink");
        googleDriveDTO.setDriveId(2L);
        googleDriveDTO.setMessage("New Message");

        assertEquals(projectDTO, googleDriveDTO.getProjectDTO());
        assertEquals("NewDriveLink", googleDriveDTO.getDriveLink());
        assertEquals(2L, googleDriveDTO.getDriveId());
        assertEquals("New Message", googleDriveDTO.getMessage());
    }

    @Test
    void testCustomConstructor() {
        GoogleDriveDTO googleDriveDTO = new GoogleDriveDTO("CustomDriveLink");

        assertNull(googleDriveDTO.getProjectDTO());
        assertEquals("CustomDriveLink", googleDriveDTO.getDriveLink());
        assertNull(googleDriveDTO.getDriveId());
        assertNull(googleDriveDTO.getMessage());
    }

    @Test
    void testToString() {
        ProjectDTO projectDTO = new ProjectDTO();
        GoogleDriveDTO googleDriveDTO = new GoogleDriveDTO(projectDTO, "DriveLink", 1L);
        googleDriveDTO.setMessage("Test Message");

        String expectedToString = "GoogleDriveDTO(projectDTO=" + projectDTO + ", driveLink=DriveLink, driveId=1, message=Test Message)";
        assertEquals(expectedToString, googleDriveDTO.toString());
    }
}
