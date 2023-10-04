package com.example.devopsproj.controller;

import com.example.devopsproj.dto.responsedto.CollaboratorDTO;
import com.example.devopsproj.service.implementations.GitHubCollaboratorServiceImpl;
import com.example.devopsproj.service.implementations.JwtServiceImpl;
import org.junit.jupiter.api.BeforeEach;
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
    private GitHubCollaboratorServiceImpl gitHubCollaboratorService;
    @Mock
    private JwtServiceImpl jwtService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ----- SUCCESS (For VALID TOKEN) -----

    @Test
    void testAddCollaborator_ValidToken_Success() {
        CollaboratorDTO collaboratorDTO = new CollaboratorDTO();
        String accessToken = "valid-access-token";

        when(jwtService.isTokenTrue(accessToken)).thenReturn(true);
        when(gitHubCollaboratorService.addCollaborator(collaboratorDTO)).thenReturn(true);

        ResponseEntity<String> response = gitHubCollaboratorController.addCollaborator(collaboratorDTO, accessToken);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Invitation to add collaborator sent successfully.", response.getBody());
    }

    @Test
    void testAddCollaborator_ValidToken_Failure() {
        CollaboratorDTO collaboratorDTO = new CollaboratorDTO();
        String accessToken = "valid-access-token";

        when(jwtService.isTokenTrue(accessToken)).thenReturn(true);
        when(gitHubCollaboratorService.addCollaborator(collaboratorDTO)).thenReturn(false);

        ResponseEntity<String> response = gitHubCollaboratorController.addCollaborator(collaboratorDTO, accessToken);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Failed to add collaborator.", response.getBody());
    }

    @Test
    void testDeleteCollaborator_ValidToken_Success() {
        CollaboratorDTO collaboratorDTO = new CollaboratorDTO();
        String accessToken = "valid-access-token";

        when(jwtService.isTokenTrue(accessToken)).thenReturn(true);
        when(gitHubCollaboratorService.deleteCollaborator(collaboratorDTO)).thenReturn(true);

        ResponseEntity<String> response = gitHubCollaboratorController.deleteCollaborator(collaboratorDTO, accessToken);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Collaborator removed successfully.", response.getBody());
    }

    @Test
    void testDeleteCollaborator_ValidToken_Failure() {
        CollaboratorDTO collaboratorDTO = new CollaboratorDTO();
        String accessToken = "valid-access-token";

        when(jwtService.isTokenTrue(accessToken)).thenReturn(true);
        when(gitHubCollaboratorService.deleteCollaborator(collaboratorDTO)).thenReturn(false);

        ResponseEntity<String> response = gitHubCollaboratorController.deleteCollaborator(collaboratorDTO, accessToken);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Failed to remove collaborator.", response.getBody());
    }


    // ----- FAILURE (For INVALID TOKEN) -----

    @Test
    void testAddCollaborator_InvalidToken() {
        CollaboratorDTO collaboratorDTO = new CollaboratorDTO();
        String accessToken = "invalid-access-token";

        when(jwtService.isTokenTrue(accessToken)).thenReturn(false);

        ResponseEntity<String> response = gitHubCollaboratorController.addCollaborator(collaboratorDTO, accessToken);

        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid Token", response.getBody());
    }

    @Test
    void testDeleteCollaborator_InvalidToken() {
        CollaboratorDTO collaboratorDTO = new CollaboratorDTO();
        String accessToken = "invalid-access-token";

        when(jwtService.isTokenTrue(accessToken)).thenReturn(false);

        ResponseEntity<String> response = gitHubCollaboratorController.deleteCollaborator(collaboratorDTO, accessToken);

        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid Token", response.getBody());
    }

}
