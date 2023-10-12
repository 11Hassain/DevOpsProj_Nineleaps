package com.example.devopsproj.controller;

import com.example.devopsproj.dto.responsedto.CollaboratorDTO;
import com.example.devopsproj.service.interfaces.GitHubCollaboratorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class GitHubCollaboratorControllerTest {

    private GitHubCollaboratorService collaboratorService;
    private GitHubCollaboratorController collaboratorController;

    @BeforeEach
    public void setUp() {
        collaboratorService = mock(GitHubCollaboratorService.class);
        collaboratorController = new GitHubCollaboratorController(collaboratorService, null); // Assuming no JwtService needed
    }

    @Test
    public void testAddCollaborator_Success() {
        // Prepare test data
        CollaboratorDTO collaboratorDTO = new CollaboratorDTO();

        // Stub the collaboratorService behavior for a non-void method
        when(collaboratorService.addCollaborator(collaboratorDTO)).thenReturn(true); // Adjust the return value as needed

        // Call the controller method
        ResponseEntity<String> response = collaboratorController.addCollaborator(collaboratorDTO);

        // Verify that collaboratorService.addCollaborator method was called once
        verify(collaboratorService, times(1)).addCollaborator(collaboratorDTO);

        // Assert the response
        assert(response.getStatusCode() == HttpStatus.OK);
        assert(response.getBody().equals("Invitation to add collaborator sent successfully."));
    }



    @Test
    public void testDeleteCollaborator_Success() {
        // Prepare test data
        CollaboratorDTO collaboratorDTO = new CollaboratorDTO();

        // Stub the collaboratorService behavior for a non-void method
        when(collaboratorService.deleteCollaborator(collaboratorDTO)).thenReturn(true); // Adjust the return value as needed

        // Call the controller method
        ResponseEntity<String> response = collaboratorController.deleteCollaborator(collaboratorDTO);

        // Verify that collaboratorService.deleteCollaborator method was called once
        verify(collaboratorService, times(1)).deleteCollaborator(collaboratorDTO);

        // Assert the response
        assert(response.getStatusCode() == HttpStatus.OK);
        assert(response.getBody().equals("Collaborator removed successfully."));
    }
}
