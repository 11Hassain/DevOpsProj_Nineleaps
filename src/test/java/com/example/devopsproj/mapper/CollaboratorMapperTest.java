package com.example.devopsproj.mapper;

import com.example.devopsproj.dto.responsedto.CollaboratorDTO;
import com.example.devopsproj.model.Collaborator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CollaboratorMapperTest {
    private CollaboratorMapper collaboratorMapper;

    @BeforeEach
    void setUp() {
        collaboratorMapper = new CollaboratorMapper();
    }

    @Test
    void testToEntity() {
        // Arrange
        CollaboratorDTO collaboratorDTO = new CollaboratorDTO();
        collaboratorDTO.setOwner("John");
        collaboratorDTO.setRepo("MyRepo");
        collaboratorDTO.setUsername("john_doe");

        // Act
        Collaborator collaborator = collaboratorMapper.toEntity(collaboratorDTO);

        // Assert
        assertEquals(collaboratorDTO.getOwner(), collaborator.getOwner());
        assertEquals(collaboratorDTO.getRepo(), collaborator.getRepo());
        assertEquals(collaboratorDTO.getUsername(), collaborator.getUsername());
    }
}
