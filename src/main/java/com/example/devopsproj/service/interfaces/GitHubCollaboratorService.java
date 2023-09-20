package com.example.devopsproj.service.interfaces;
import com.example.devopsproj.dto.responseDto.CollaboratorDTO;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
public interface GitHubCollaboratorService {

    boolean addCollaborator(CollaboratorDTO collaboratorDTO);

    boolean deleteCollaborator(CollaboratorDTO collaboratorDTO);
}