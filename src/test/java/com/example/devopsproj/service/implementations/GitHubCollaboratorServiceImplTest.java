package com.example.devopsproj.service.implementations;

import com.example.devopsproj.dto.responsedto.CollaboratorDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class GitHubCollaboratorServiceImplTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private GitHubCollaboratorServiceImpl gitHubCollaboratorService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

//    @Test
//    public void testAddCollaborator_Success() {
//        // Arrange
//        CollaboratorDTO collaboratorDTO = new CollaboratorDTO("owner", "repo", "username", "access-token");
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setBearerAuth("access-token");
//
//        ResponseEntity<String> successResponse = new ResponseEntity<>("Success", HttpStatus.OK);
//
//        when(restTemplate.exchange(
//                eq("https://api.github.com/repos/owner/repo/collaborators/username"),
//                eq(HttpMethod.PUT),
//                any(HttpEntity.class),
//                eq(String.class)))
//                .thenReturn(successResponse);
//
//        // Act
//        boolean result = gitHubCollaboratorService.addCollaborator(collaboratorDTO);
//
//        // Assert
//        assertTrue(result);
//
//        // Verify that restTemplate.exchange is called with the correct parameters
//        verify(restTemplate, times(1)).exchange(
//                eq("https://api.github.com/repos/owner/repo/collaborators/username"),
//                eq(HttpMethod.PUT),
//                any(HttpEntity.class),
//                eq(String.class));
//    }
//
//
//    @Test
//    public void testAddCollaborator_Failure_Unauthorized() {
//        // Arrange
//        CollaboratorDTO collaboratorDTO = new CollaboratorDTO("owner", "repo", "username", "invalid-access-token");
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setBearerAuth("invalid-access-token");
//
//        ResponseEntity<String> unauthorizedResponse = new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
//
//        when(restTemplate.exchange(
//                eq("https://api.github.com/repos/owner/repo/collaborators/username"),
//                eq(HttpMethod.PUT),
//                any(HttpEntity.class),
//                eq(String.class)))
//                .thenThrow(new HttpClientErrorException(HttpStatus.UNAUTHORIZED, "Unauthorized"));
//
//        // Act
//        boolean result = gitHubCollaboratorService.addCollaborator(collaboratorDTO);
//
//        // Assert
//        assertFalse(result);
//    }

}
