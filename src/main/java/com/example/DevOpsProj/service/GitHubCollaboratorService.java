package com.example.DevOpsProj.service;
import com.example.DevOpsProj.dto.responseDto.CollaboratorDTO;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
@Service
public class GitHubCollaboratorService {

    public boolean addCollaborator(CollaboratorDTO collaboratorDTO) {
        String apiUrl = String.format("https://api.github.com/repos/%s/%s/collaborators/%s",
                collaboratorDTO.getOwner(), collaboratorDTO.getRepo(), collaboratorDTO.getUsername());

        HttpHeaders headers = createHttpHeaders(collaboratorDTO.getAccessToken());

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.exchange(apiUrl, HttpMethod.PUT, requestEntity, String.class);

        return responseEntity.getStatusCode().is2xxSuccessful();
    }

    public boolean deleteCollaborator(CollaboratorDTO collaboratorDTO) {
        String apiUrl = String.format("https://api.github.com/repos/%s/%s/collaborators/%s",
                collaboratorDTO.getOwner(), collaboratorDTO.getRepo(), collaboratorDTO.getUsername());

        HttpHeaders headers = createHttpHeaders(collaboratorDTO.getAccessToken());

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.exchange(apiUrl, HttpMethod.DELETE, requestEntity, String.class);

        return responseEntity.getStatusCode().is2xxSuccessful();
    }

    private HttpHeaders createHttpHeaders(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.set("Accept", "application/vnd.github+json");
        headers.set("X-GitHub-Api-Version", "2022-11-28");
        return headers;
    }
}
