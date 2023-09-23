package com.example.devopsproj.service.interfaces;

import com.example.devopsproj.dto.responseDto.CollaboratorDTO;
import org.springframework.http.HttpHeaders;

public interface GitHubCollaboratorService {
    boolean addCollaborator(CollaboratorDTO collaboratorDTO);

    boolean deleteCollaborator(CollaboratorDTO collaboratorDTO);

    HttpHeaders createHttpHeaders(String accessToken);
}
