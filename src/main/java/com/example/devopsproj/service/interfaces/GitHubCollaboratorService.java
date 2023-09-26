package com.example.devopsproj.service.interfaces;
import com.example.devopsproj.dto.responsedto.CollaboratorDTO;

public interface GitHubCollaboratorService {

    boolean addCollaborator(CollaboratorDTO collaboratorDTO);

    boolean deleteCollaborator(CollaboratorDTO collaboratorDTO);
}