package com.example.devopsproj.mapper;
/**
 * CollaboratorMapper is a class responsible for mapping CollaboratorDTO objects to Collaborator entities.
 * It is annotated with @Component to indicate that it is a Spring bean and can be automatically detected
 * and instantiated by the Spring framework.
 */
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
