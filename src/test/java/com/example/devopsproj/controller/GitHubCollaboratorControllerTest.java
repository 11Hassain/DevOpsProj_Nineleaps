package com.example.devopsproj.controller;

import com.example.devopsproj.dto.responsedto.CollaboratorDTO;
import com.example.devopsproj.service.interfaces.GitHubCollaboratorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GitHubCollaboratorControllerTest {

    @InjectMocks
    private GitHubCollaboratorController gitHubCollaboratorController;
    @Mock
    private GitHubCollaboratorService gitHubCollaboratorService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    class AddCollaboratorTest {
        @Test
        @DisplayName("Testing success case with valid token")
        void testAddCollaborator_ValidToken_Success() {
            CollaboratorDTO collaboratorDTO = new CollaboratorDTO();

            when(gitHubCollaboratorService.addCollaborator(collaboratorDTO)).thenReturn(true);

            ResponseEntity<String> response = gitHubCollaboratorController.addCollaborator(collaboratorDTO);

            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("Invitation to add collaborator sent successfully.", response.getBody());
        }

        @Test
        @DisplayName("Testing failure case (Bad request)")
        void testAddCollaborator_ValidToken_Failure() {
            CollaboratorDTO collaboratorDTO = new CollaboratorDTO();

            when(gitHubCollaboratorService.addCollaborator(collaboratorDTO)).thenReturn(false);

            ResponseEntity<String> response = gitHubCollaboratorController.addCollaborator(collaboratorDTO);

            assertNotNull(response);
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertEquals("Failed to add collaborator.", response.getBody());
        }
    }

    @Nested
    class DeleteCollaboratorTest {
        @Test
        @DisplayName("Testing success case with valid token")
        void testDeleteCollaborator_ValidToken_Success() {
            CollaboratorDTO collaboratorDTO = new CollaboratorDTO();

            when(gitHubCollaboratorService.deleteCollaborator(collaboratorDTO)).thenReturn(true);

            ResponseEntity<String> response = gitHubCollaboratorController.deleteCollaborator(collaboratorDTO);

            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("Collaborator removed successfully.", response.getBody());
        }

        @Test
        @DisplayName("Testing failure case (Bad request)")
        void testDeleteCollaborator_ValidToken_Failure() {
            CollaboratorDTO collaboratorDTO = new CollaboratorDTO();

            when(gitHubCollaboratorService.deleteCollaborator(collaboratorDTO)).thenReturn(false);

            ResponseEntity<String> response = gitHubCollaboratorController.deleteCollaborator(collaboratorDTO);

            assertNotNull(response);
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertEquals("Failed to remove collaborator.", response.getBody());
        }
    }
}
