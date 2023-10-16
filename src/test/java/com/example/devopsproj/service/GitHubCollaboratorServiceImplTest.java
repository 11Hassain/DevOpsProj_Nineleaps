package com.example.devopsproj.service;

import com.example.devopsproj.dto.responsedto.CollaboratorDTO;
import com.example.devopsproj.service.implementations.GitHubCollaboratorServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import org.springframework.http.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GitHubCollaboratorServiceImplTest {

    @InjectMocks
    private GitHubCollaboratorServiceImpl gitHubCollaboratorService;
    @Mock
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateHttpHeaders() {
        String accessToken = "your-access-token";

        HttpHeaders headers = gitHubCollaboratorService.createHttpHeaders(accessToken);

        assertNotNull(headers);
        assertEquals("Bearer " + accessToken, headers.getFirst("Authorization"));
        assertEquals("application/vnd.github+json", headers.getFirst("Accept"));
        assertEquals("2022-11-28", headers.getFirst("X-GitHub-Api-Version"));
    }

    @Nested
    class AddCollaboratorTest {
        @Test
        @DisplayName("Testing failure case - Unauthorized")
        void testAddCollaborator_Unauthorized() {
            when(restTemplate.exchange(anyString(), eq(HttpMethod.PUT), any(HttpEntity.class), eq(String.class)))
                    .thenReturn(new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED));

            CollaboratorDTO collaboratorDTO = new CollaboratorDTO("owner", "repo", "username", "accessToken");

            boolean result = gitHubCollaboratorService.addCollaborator(collaboratorDTO);

            assertFalse(result);
        }

        @Test
        @DisplayName("Testing failure case - Collaborator not found")
        void testAddCollaborator_CollaboratorNotFound() {
            when(restTemplate.exchange(anyString(), eq(HttpMethod.PUT), any(HttpEntity.class), eq(String.class)))
                    .thenReturn(new ResponseEntity<>("Not Found", HttpStatus.NOT_FOUND));

            CollaboratorDTO collaboratorDTO = new CollaboratorDTO("owner", "repo", "username", "accessToken");

            boolean result = gitHubCollaboratorService.addCollaborator(collaboratorDTO);

            assertFalse(result);
        }

        @Test
        @DisplayName("Testing failure case - Http Error")
        void testAddCollaborator_HttpError() {
            HttpClientErrorException httpClientErrorException = new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request");
            when(restTemplate.exchange(anyString(), eq(HttpMethod.PUT), any(HttpEntity.class), eq(String.class)))
                    .thenThrow(httpClientErrorException);

            CollaboratorDTO collaboratorDTO = new CollaboratorDTO("owner", "repo", "username", "accessToken");

            boolean result = gitHubCollaboratorService.addCollaborator(collaboratorDTO);

            assertFalse(result);
        }
    }

    @Nested
    class DeleteCollaboratorTest {
        @Test
        @DisplayName("Testing failure case - Unauthorized")
        void testDeleteCollaborator_Unsuccessful() {
            HttpClientErrorException httpClientErrorException = new HttpClientErrorException(HttpStatus.UNAUTHORIZED, "Unauthorized");
            when(restTemplate.exchange(anyString(), eq(HttpMethod.DELETE), any(HttpEntity.class), eq(String.class)))
                    .thenThrow(httpClientErrorException);

            CollaboratorDTO collaboratorDTO = new CollaboratorDTO("owner", "repo", "username", "accessToken");

            boolean result = gitHubCollaboratorService.deleteCollaborator(collaboratorDTO);

            assertFalse(result);
        }

        @Test
        @DisplayName("Testing failure case - Http Error")
        void testDeleteCollaborator_HttpError() {
            HttpClientErrorException httpClientErrorException = new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request");
            when(restTemplate.exchange(anyString(), eq(HttpMethod.DELETE), any(HttpEntity.class), eq(String.class)))
                    .thenThrow(httpClientErrorException);

            CollaboratorDTO collaboratorDTO = new CollaboratorDTO("owner", "repo", "username", "accessToken");

            boolean result = gitHubCollaboratorService.deleteCollaborator(collaboratorDTO);

            assertFalse(result);
        }
    }
}
