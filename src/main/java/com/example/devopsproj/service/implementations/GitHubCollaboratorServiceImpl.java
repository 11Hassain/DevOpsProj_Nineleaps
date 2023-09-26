package com.example.devopsproj.service.implementations;

import com.example.devopsproj.dto.responsedto.CollaboratorDTO;
import com.example.devopsproj.service.interfaces.GitHubCollaboratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class GitHubCollaboratorServiceImpl implements GitHubCollaboratorService {

    @Override
    public boolean addCollaborator(CollaboratorDTO collaboratorDTO) {
        try {
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
        } catch (Exception e) {
            // Handle exceptions here, e.g., log the error or throw a custom exception
            // You can create a custom exception like GitHubCollaboratorException if needed
            return false; // Return false to indicate failure
        }
    }

    @Override
    public boolean deleteCollaborator(CollaboratorDTO collaboratorDTO) {
        try {
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
        } catch (Exception e) {
            // Handle exceptions here, e.g., log the error or throw a custom exception
            // You can create a custom exception like GitHubCollaboratorException if needed
            return false; // Return false to indicate failure
        }
    }

    // Helper method to create HTTP headers with the access token and required GitHub API headers
    private HttpHeaders createHttpHeaders(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.set("Accept", "application/vnd.github.v3+json");
        // Add other required headers if necessary
        return headers;
    }
}
