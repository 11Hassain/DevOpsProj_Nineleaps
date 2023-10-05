package com.example.devopsproj.service;

import com.example.devopsproj.service.implementations.GitHubCollaboratorServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
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

    // ----- SUCCESS -----

    @Test
    void testCreateHttpHeaders() {
        String accessToken = "your-access-token";

        HttpHeaders headers = gitHubCollaboratorService.createHttpHeaders(accessToken);

        assertNotNull(headers);
        assertEquals("Bearer " + accessToken, headers.getFirst("Authorization"));
        assertEquals("application/vnd.github+json", headers.getFirst("Accept"));
        assertEquals("2022-11-28", headers.getFirst("X-GitHub-Api-Version"));
    }

    // ----- FAILURE -----


}
