package com.example.devopsproj.service.implementations;

import com.example.devopsproj.dto.responseDto.CollaboratorDTO;
import com.example.devopsproj.service.interfaces.GitHubCollaboratorService;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GitHubCollaboratorServiceImpl implements GitHubCollaboratorService {

    // Add a collaborator to a GitHub repository
    @Override
    public boolean addCollaborator(CollaboratorDTO collaboratorDTO) {
        // Construct the GitHub API URL for adding a collaborator
        String apiUrl = String.format("https://api.github.com/repos/%s/%s/collaborators/%s",
                collaboratorDTO.getOwner(), collaboratorDTO.getRepo(), collaboratorDTO.getUsername());

        // Create HTTP headers with the provided access token and required GitHub API headers
        HttpHeaders headers = createHttpHeaders(collaboratorDTO.getAccessToken());

        // Create an HTTP request entity with the headers
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        // Create a RestTemplate to send the HTTP request
        RestTemplate restTemplate = new RestTemplate();

        // Send an HTTP PUT request to add the collaborator
        ResponseEntity<String> responseEntity = restTemplate.exchange(apiUrl, HttpMethod.PUT, requestEntity, String.class);

        // Check if the response status code indicates success (2xx)
        return responseEntity.getStatusCode().is2xxSuccessful();
    }

    // Remove a collaborator from a GitHub repository
    @Override
    public boolean deleteCollaborator(CollaboratorDTO collaboratorDTO) {
        // Construct the GitHub API URL for removing a collaborator
        String apiUrl = String.format("https://api.github.com/repos/%s/%s/collaborators/%s",
                collaboratorDTO.getOwner(), collaboratorDTO.getRepo(), collaboratorDTO.getUsername());

        // Create HTTP headers with the provided access token and required GitHub API headers
        HttpHeaders headers = createHttpHeaders(collaboratorDTO.getAccessToken());

        // Create an HTTP request entity with the headers
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        // Create a RestTemplate to send the HTTP request
        RestTemplate restTemplate = new RestTemplate();

        // Send an HTTP DELETE request to remove the collaborator
        ResponseEntity<String> responseEntity = restTemplate.exchange(apiUrl, HttpMethod.DELETE, requestEntity, String.class);

        // Check if the response status code indicates success (2xx)
        return responseEntity.getStatusCode().is2xxSuccessful();
    }

    // Helper method to create HTTP headers with required GitHub API headers
    private HttpHeaders createHttpHeaders(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.set("Accept", "application/vnd.github+json");
        headers.set("X-GitHub-Api-Version", "2022-11-28");
        return headers;
    }
}