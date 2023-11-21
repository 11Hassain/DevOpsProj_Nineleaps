package com.example.devopsproj.service.implementations;

import com.example.devopsproj.dto.responsedto.CollaboratorDTO;
import com.example.devopsproj.service.interfaces.GitHubCollaboratorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
/**
 * Service implementation for managing GitHub collaborators.
 */
@Service
public class GitHubCollaboratorServiceImpl implements GitHubCollaboratorService {

    private static final Logger logger = LoggerFactory.getLogger(GitHubCollaboratorServiceImpl.class);

    /**
     * Adds a collaborator to a GitHub repository.
     *
     * @param collaboratorDTO the DTO containing information about the collaborator.
     * @return true if the collaborator was successfully added, false otherwise.
     */
    @Override
    public boolean addCollaborator(CollaboratorDTO collaboratorDTO) {
        String apiUrl = String.format("https://api.github.com/repos/%s/%s/collaborators/%s",
                collaboratorDTO.getOwner(), collaboratorDTO.getRepo(), collaboratorDTO.getUsername());

        HttpHeaders headers = createHttpHeaders(collaboratorDTO.getAccessToken());

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> responseEntity = restTemplate.exchange(apiUrl, HttpMethod.PUT, requestEntity, String.class);

            // Check the response status code
            return responseEntity.getStatusCode().is2xxSuccessful();
        } catch (HttpClientErrorException ex) {
            // Handle HTTP errors, e.g., log the error
            int statusCode = ex.getStatusCode().value();
            logger.error("GitHub API returned an error: {} {}", statusCode, ex.getStatusText());
            return false;
        }
    }
    /**
     * Deletes a collaborator from a GitHub repository.
     *
     * @param collaboratorDTO the DTO containing information about the collaborator.
     * @return true if the collaborator was successfully deleted, false otherwise.
     */
    @Override
    public boolean deleteCollaborator(CollaboratorDTO collaboratorDTO) {
        String apiUrl = String.format("https://api.github.com/repos/%s/%s/collaborators/%s",
                collaboratorDTO.getOwner(), collaboratorDTO.getRepo(), collaboratorDTO.getUsername());

        HttpHeaders headers = createHttpHeaders(collaboratorDTO.getAccessToken());

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> responseEntity = restTemplate.exchange(apiUrl, HttpMethod.DELETE, requestEntity, String.class);

            return responseEntity.getStatusCode().is2xxSuccessful();
        } catch (HttpClientErrorException ex){
            // Handle HTTP errors, e.g., log the error
            int statusCode = ex.getStatusCode().value();
            logger.error("GitHub API returned an error: {} {}", statusCode, ex.getStatusText());
            return false;
        }
    }


    /**
     * Creates HTTP headers for making requests to the GitHub API with the provided access token.
     *
     * @param accessToken the GitHub access token.
     * @return HttpHeaders with the necessary headers for GitHub API requests.
     */
    @Override
    public HttpHeaders createHttpHeaders(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.set("Accept", "application/vnd.github+json");
        headers.set("X-GitHub-Api-Version", "2022-11-28");

        // Log information about the headers being created
        logger.debug("Created HTTP headers with access token for GitHub API request.");

        return headers;
    }
}

