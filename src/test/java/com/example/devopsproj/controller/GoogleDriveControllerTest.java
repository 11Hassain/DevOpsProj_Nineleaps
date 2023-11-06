package com.example.devopsproj.controller;

import com.example.devopsproj.dto.responsedto.GoogleDriveDTO;
import com.example.devopsproj.dto.responsedto.ProjectDTO;
import com.example.devopsproj.model.GoogleDrive;
import com.example.devopsproj.model.Project;
import com.example.devopsproj.service.interfaces.GoogleDriveService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class GoogleDriveControllerTest {

    @InjectMocks
    private GoogleDriveController googleDriveController;
    @Mock
    private GoogleDriveService googleDriveService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Testing success case with valid token")
    void testCreateGoogleDrive_ValidToken_Success() {
        String accessToken = "valid-access-token";
        GoogleDriveDTO googleDriveDTO = new GoogleDriveDTO();
        googleDriveDTO.setDriveLink("https://drive.google.com");
        googleDriveDTO.setDriveId(1L);

        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setProjectId(1L);
        projectDTO.setProjectName("Sample Project");
        googleDriveDTO.setProjectDTO(projectDTO);

        GoogleDrive createdGoogleDrive = new GoogleDrive();
        createdGoogleDrive.setDriveLink(googleDriveDTO.getDriveLink());
        createdGoogleDrive.setDriveId(googleDriveDTO.getDriveId());

        Project project = new Project();
        project.setProjectId(projectDTO.getProjectId());
        project.setProjectName(projectDTO.getProjectName());
        createdGoogleDrive.setProject(project);

        when(googleDriveService.createGoogleDrive(googleDriveDTO)).thenReturn(createdGoogleDrive);

        ResponseEntity<Object> response = googleDriveController.createGoogleDrive(googleDriveDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        GoogleDriveDTO expectedGoogleDriveDTO = new GoogleDriveDTO(
                new ProjectDTO(projectDTO.getProjectId(), projectDTO.getProjectName()),
                googleDriveDTO.getDriveLink(),
                googleDriveDTO.getDriveId()
        );

        // Unwrap the response body and cast it to GoogleDriveDTO
        GoogleDriveDTO actualGoogleDriveDTO = (GoogleDriveDTO) response.getBody();

        // Compare individual fields
        assertNotNull(actualGoogleDriveDTO);
        assertEquals(expectedGoogleDriveDTO.getDriveLink(), actualGoogleDriveDTO.getDriveLink());
        assertEquals(expectedGoogleDriveDTO.getDriveId(), actualGoogleDriveDTO.getDriveId());

        // Compare the ProjectDTO fields within GoogleDriveDTO
        assertEquals(expectedGoogleDriveDTO.getProjectDTO().getProjectId(), actualGoogleDriveDTO.getProjectDTO().getProjectId());
        assertEquals(expectedGoogleDriveDTO.getProjectDTO().getProjectName(), actualGoogleDriveDTO.getProjectDTO().getProjectName());
    }

    @Nested
    class GetAllGoogleDrivesTest {
        @Test
        @DisplayName("Testing success case with valid token")
        void testGetAllGoogleDrives_ValidToken() {
            List<GoogleDrive> googleDrives = new ArrayList<>();

            // Create and associate valid Project objects with GoogleDrive objects
            Project project1 = new Project();
            project1.setProjectId(1L);
            project1.setProjectName("Project 1");
            GoogleDrive googleDrive1 = new GoogleDrive();
            googleDrive1.setProject(project1);
            googleDrives.add(googleDrive1);

            Project project2 = new Project();
            project2.setProjectId(2L);
            project2.setProjectName("Project 2");
            GoogleDrive googleDrive2 = new GoogleDrive();
            googleDrive2.setProject(project2);
            googleDrives.add(googleDrive2);

            when(googleDriveService.getAllGoogleDrives()).thenReturn(googleDrives);

            ResponseEntity<Object> response = googleDriveController.getAllGoogleDrives();

            List<GoogleDriveDTO> responseGoogleDriveDTOs = (List<GoogleDriveDTO>) response.getBody();

            assertNotNull(responseGoogleDriveDTOs);
            assertEquals(2, responseGoogleDriveDTOs.size());
        }

        @Test
        @DisplayName("Testing empty Google drive list case")
        void testGetAllGoogleDrives_ValidToken_EmptyList(){
            when(googleDriveService.getAllGoogleDrives()).thenReturn(Collections.emptyList());

            ResponseEntity<Object> response = googleDriveController.getAllGoogleDrives();

            List<GoogleDriveDTO> responseGoogleDriveDTOs = (List<GoogleDriveDTO>) response.getBody();

            assertNotNull(responseGoogleDriveDTOs);
            assertTrue(responseGoogleDriveDTOs.isEmpty());
        }
    }

    @Nested
    class GetGoogleDriveByIdTest {
        @Test
        @DisplayName("Testing success case with valid token")
        void testGetGoogleDriveById_ValidToken_ExistingDrive() {
            Long driveId = 1L;

            GoogleDriveDTO existingGoogleDriveDTO = new GoogleDriveDTO();
            existingGoogleDriveDTO.setDriveId(driveId);

            when(googleDriveService.getGoogleDriveById(driveId)).thenReturn(Optional.of(existingGoogleDriveDTO));

            ResponseEntity<GoogleDriveDTO> response = googleDriveController.getGoogleDriveById(driveId);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(existingGoogleDriveDTO, response.getBody());
        }

        @Test
        @DisplayName("Testing non-existent drive case")
        void testGetGoogleDriveById_ValidToken_NonExistingDrive() {
            Long driveId = 1L;

            when(googleDriveService.getGoogleDriveById(driveId)).thenReturn(Optional.empty());

            ResponseEntity<GoogleDriveDTO> response = googleDriveController.getGoogleDriveById(driveId);

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertNull(response.getBody());
        }
    }

    @Nested
    class DeleteGoogleDriveByIdTest {
        @Test
        @DisplayName("Testing success case with valid token")
        void testDeleteGoogleDriveById_ValidToken_DeletedDrive() {
            Long driveId = 1L;
            when(googleDriveService.deleteGoogleDriveById(driveId)).thenReturn(true);

            ResponseEntity<String> response = googleDriveController.deleteGoogleDriveById(driveId);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("Google Drive with ID: " + driveId + " deleted successfully.", response.getBody());
        }

        @Test
        @DisplayName("Testing non-existent drive case")
        void testDeleteGoogleDriveById_ValidToken_NonExistingDrive() {
            Long driveId = 1L;
            when(googleDriveService.deleteGoogleDriveById(driveId)).thenReturn(false);

            ResponseEntity<String> response = googleDriveController.deleteGoogleDriveById(driveId);

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertNull(response.getBody());
        }
    }

    @Nested
    class GetGoogleDriveByProjectIdTest {
        @Test
        @DisplayName("Testing success case with valid token")
        void testGetGoogleDriveByProjectId_ValidToken_ExistingDrive() {
            Long projectId = 1L;

            GoogleDrive existingGoogleDrive = new GoogleDrive();
            existingGoogleDrive.setDriveLink("https://drive.google.com");
            existingGoogleDrive.setDriveId(1L);
            Project associatedProject = new Project();
            associatedProject.setProjectId(projectId);
            associatedProject.setProjectName("Project 1");
            existingGoogleDrive.setProject(associatedProject);

            when(googleDriveService.getGoogleDriveByProjectId(projectId)).thenReturn(Optional.of(existingGoogleDrive));

            ResponseEntity<GoogleDriveDTO> response = googleDriveController.getGoogleDriveByProjectId(projectId);

            assertEquals(HttpStatus.OK, response.getStatusCode());

            // Verify the content of the response
            GoogleDriveDTO googleDriveDTO = response.getBody();
            assertNotNull(googleDriveDTO);
            assertEquals(existingGoogleDrive.getDriveLink(), googleDriveDTO.getDriveLink());
            assertEquals(existingGoogleDrive.getDriveId(), googleDriveDTO.getDriveId());
            assertEquals(associatedProject.getProjectId(), googleDriveDTO.getProjectDTO().getProjectId());
            assertEquals(associatedProject.getProjectName(), googleDriveDTO.getProjectDTO().getProjectName());
        }

        @Test
        @DisplayName("Testing non-existent drive case")
        void testGetGoogleDriveByProjectId_ValidToken_NonExistingDrive() {
            Long projectId = 1L;
            when(googleDriveService.getGoogleDriveByProjectId(projectId)).thenReturn(Optional.empty());

            ResponseEntity<GoogleDriveDTO> response = googleDriveController.getGoogleDriveByProjectId(projectId);

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertNull(response.getBody());
        }
    }
}
