package com.example.devopsproj.controller;

import com.example.devopsproj.dto.responsedto.GoogleDriveDTO;
import com.example.devopsproj.dto.responsedto.ProjectDTO;
import com.example.devopsproj.model.GoogleDrive;
import com.example.devopsproj.model.Project;
import com.example.devopsproj.service.implementations.GoogleDriveServiceImpl;
import com.example.devopsproj.service.implementations.JwtServiceImpl;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class GoogleDriveControllerTest {

    @InjectMocks
    private GoogleDriveController googleDriveController;
    @Mock
    private GoogleDriveServiceImpl googleDriveService;
    @Mock
    private JwtServiceImpl jwtService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private static final String INVALID_TOKEN = "Invalid Token";

    @Nested
    class CreateGoogleDriveTest {
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

            when(jwtService.isTokenTrue(accessToken)).thenReturn(true);
            when(googleDriveService.createGoogleDrive(googleDriveDTO)).thenReturn(createdGoogleDrive);

            ResponseEntity<Object> response = googleDriveController.createGoogleDrive(googleDriveDTO, accessToken);

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

        @Test
        @DisplayName("Testing failure case with invalid token")
        void testCreateGoogleDrive_InvalidToken(){
            GoogleDriveDTO googleDriveDTO = new GoogleDriveDTO();
            googleDriveDTO.setDriveLink("https://www.googleDriveLink.com");

            when(jwtService.isTokenTrue(anyString())).thenReturn(false);

            ResponseEntity<Object> response = googleDriveController.createGoogleDrive(googleDriveDTO, "invalid-access-token");

            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            assertEquals("Invalid Token", response.getBody());
        }
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

            when(jwtService.isTokenTrue(anyString())).thenReturn(true);
            when(googleDriveService.getAllGoogleDrives()).thenReturn(googleDrives);

            ResponseEntity<Object> response = googleDriveController.getAllGoogleDrives("valid-access-token");

            List<GoogleDriveDTO> responseGoogleDriveDTOs = (List<GoogleDriveDTO>) response.getBody();

            assertNotNull(responseGoogleDriveDTOs);
            assertEquals(2, responseGoogleDriveDTOs.size());
        }

        @Test
        @DisplayName("Testing empty Google drive list case")
        void testGetAllGoogleDrives_ValidToken_EmptyList(){
            when(jwtService.isTokenTrue(anyString())).thenReturn(true);
            when(googleDriveService.getAllGoogleDrives()).thenReturn(Collections.emptyList());

            ResponseEntity<Object> response = googleDriveController.getAllGoogleDrives("valid-access-token");

            List<GoogleDriveDTO> responseGoogleDriveDTOs = (List<GoogleDriveDTO>) response.getBody();

            assertNotNull(responseGoogleDriveDTOs);
            assertTrue(responseGoogleDriveDTOs.isEmpty());
        }

        @Test
        @DisplayName("Testing failure case with invalid token")
        void testGetAllGoogleDrives_InvalidToken(){
            when(jwtService.isTokenTrue(anyString())).thenReturn(false);

            ResponseEntity<Object> response = googleDriveController.getAllGoogleDrives("invalid-access-token");

            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            assertEquals("Invalid Token", response.getBody());
        }
    }

    @Nested
    class GetGoogleDriveByIdTest {
        @Test
        @DisplayName("Testing success case with valid token")
        void testGetGoogleDriveById_ValidToken_ExistingDrive() {
            Long driveId = 1L;
            when(jwtService.isTokenTrue(anyString())).thenReturn(true);

            GoogleDriveDTO existingGoogleDriveDTO = new GoogleDriveDTO();
            existingGoogleDriveDTO.setDriveId(driveId);

            when(googleDriveService.getGoogleDriveById(driveId)).thenReturn(Optional.of(existingGoogleDriveDTO));

            ResponseEntity<GoogleDriveDTO> response = googleDriveController.getGoogleDriveById(driveId, "valid-access-token");

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(existingGoogleDriveDTO, response.getBody());
        }

        @Test
        @DisplayName("Testing non-existent drive case")
        void testGetGoogleDriveById_ValidToken_NonExistingDrive() {
            Long driveId = 1L;

            when(jwtService.isTokenTrue(anyString())).thenReturn(true);
            when(googleDriveService.getGoogleDriveById(driveId)).thenReturn(Optional.empty());

            ResponseEntity<GoogleDriveDTO> response = googleDriveController.getGoogleDriveById(driveId, "valid-access-token");

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertNull(response.getBody());
        }

        @Test
        @DisplayName("Testing failure case with invalid token")
        void testGetGoogleDriveById_InvalidToken(){
            Long driveId = 1L;

            when(jwtService.isTokenTrue(anyString())).thenReturn(false);

            ResponseEntity<GoogleDriveDTO> response = googleDriveController.getGoogleDriveById(driveId,"invalid-access-token");

            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        }
    }

    @Nested
    class DeleteGoogleDriveByIdTest {
        @Test
        @DisplayName("Testing success case with valid token")
        void testDeleteGoogleDriveById_ValidToken_DeletedDrive() {
            Long driveId = 1L;
            when(jwtService.isTokenTrue(anyString())).thenReturn(true);
            when(googleDriveService.deleteGoogleDriveById(driveId)).thenReturn(true);

            ResponseEntity<String> response = googleDriveController.deleteGoogleDriveById(driveId, "valid-access-token");

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("Google Drive with ID: " + driveId + " deleted successfully.", response.getBody());
        }

        @Test
        @DisplayName("Testing non-existent drive case")
        void testDeleteGoogleDriveById_ValidToken_NonExistingDrive() {
            Long driveId = 1L;
            when(jwtService.isTokenTrue(anyString())).thenReturn(true);
            when(googleDriveService.deleteGoogleDriveById(driveId)).thenReturn(false);

            ResponseEntity<String> response = googleDriveController.deleteGoogleDriveById(driveId, "valid-access-token");

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertNull(response.getBody());
        }

        @Test
        @DisplayName("Testing failure case with invalid token")
        void testDeleteGoogleDriveById_InvalidToken(){
            Long driveId = 1L;

            when(jwtService.isTokenTrue(anyString())).thenReturn(false);

            ResponseEntity<String> response = googleDriveController.deleteGoogleDriveById(driveId,"invalid-access-token");

            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            assertEquals("Invalid Token", response.getBody());
        }
    }

    @Nested
    class GetGoogleDriveByProjectIdTest {
        @Test
        @DisplayName("Testing success case with valid token")
        void testGetGoogleDriveByProjectId_ValidToken_ExistingDrive() {
            Long projectId = 1L;
            when(jwtService.isTokenTrue(anyString())).thenReturn(true);

            GoogleDrive existingGoogleDrive = new GoogleDrive();
            existingGoogleDrive.setDriveLink("https://drive.google.com");
            existingGoogleDrive.setDriveId(1L);
            Project associatedProject = new Project();
            associatedProject.setProjectId(projectId);
            associatedProject.setProjectName("Project 1");
            existingGoogleDrive.setProject(associatedProject);

            when(googleDriveService.getGoogleDriveByProjectId(projectId)).thenReturn(Optional.of(existingGoogleDrive));

            ResponseEntity<GoogleDriveDTO> response = googleDriveController.getGoogleDriveByProjectId(projectId, "valid-access-token");

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
            when(jwtService.isTokenTrue(anyString())).thenReturn(true);
            when(googleDriveService.getGoogleDriveByProjectId(projectId)).thenReturn(Optional.empty());

            ResponseEntity<GoogleDriveDTO> response = googleDriveController.getGoogleDriveByProjectId(projectId, "valid-access-token");

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertNull(response.getBody());
        }

        @Test
        @DisplayName("Testing failure case with invalid token")
        void testGetGoogleDriveByProjectId_InvalidToken(){
            Long projectId = 1L;

            when(jwtService.isTokenTrue(anyString())).thenReturn(false);

            ResponseEntity<GoogleDriveDTO> response = googleDriveController.getGoogleDriveByProjectId(projectId,"invalid-access-token");

            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(INVALID_TOKEN, response.getBody().getMessage());
        }
    }
}
