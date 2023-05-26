package com.example.DevOpsProj.Mapper;

import com.example.DevOpsProj.dto.responseDto.CollaboratorDTO;
import com.example.DevOpsProj.model.Collaborator;
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
