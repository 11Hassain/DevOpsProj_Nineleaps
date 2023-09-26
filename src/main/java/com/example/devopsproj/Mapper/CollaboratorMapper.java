package com.example.devopsproj.Mapper;

import com.example.devopsproj.dto.responsedto.CollaboratorDTO;
import com.example.devopsproj.model.Collaborator;
import org.springframework.stereotype.Component;

@Component
public class CollaboratorMapper {

    public Collaborator toEntity(CollaboratorDTO collaboratorDTO) {
        Collaborator collaborator = new Collaborator();
        collaborator.setOwner(collaboratorDTO.getOwner());
        collaborator.setRepo(collaboratorDTO.getRepo());
        collaborator.setUsername(collaboratorDTO.getUsername());
        return collaborator;
    }
}
