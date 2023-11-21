package com.example.devopsproj.service.interfaces;
import com.example.devopsproj.dto.responsedto.CollaboratorDTO;
import org.springframework.http.HttpHeaders;
/**
 * Service interface for managing GitHub collaborator-related operations, including
 * adding and deleting collaborators, as well as creating HTTP headers for GitHub API requests.
 */

public interface GitHubCollaboratorService {

    boolean addCollaborator(CollaboratorDTO collaboratorDTO);

    boolean deleteCollaborator(CollaboratorDTO collaboratorDTO);

    HttpHeaders createHttpHeaders(String accessToken);
}