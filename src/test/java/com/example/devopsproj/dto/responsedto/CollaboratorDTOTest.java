package com.example.devopsproj.dto.responsedto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CollaboratorDTOTest {

    @Test
    void testValidCollaboratorDTO() {
        CollaboratorDTO collaboratorDTO = new CollaboratorDTO();
        collaboratorDTO.setOwner("owner");
        collaboratorDTO.setRepo("repo");
        collaboratorDTO.setUsername("username");
        collaboratorDTO.setAccessToken("accessToken");

        assertEquals("owner", collaboratorDTO.getOwner());
        assertEquals("repo", collaboratorDTO.getRepo());
        assertEquals("username", collaboratorDTO.getUsername());
        assertEquals("accessToken", collaboratorDTO.getAccessToken());
    }

    @Test
    void testNoArgsConstructor() {
        CollaboratorDTO collaboratorDTO = new CollaboratorDTO();

        assertNull(collaboratorDTO.getOwner());
        assertNull(collaboratorDTO.getRepo());
        assertNull(collaboratorDTO.getUsername());
        assertNull(collaboratorDTO.getAccessToken());
    }

    @Test
    void testAllArgsConstructor() {
        CollaboratorDTO collaboratorDTO = new CollaboratorDTO("JohnDoe", "example-repo", "johnDoe", "token123");

        assertEquals("JohnDoe", collaboratorDTO.getOwner());
        assertEquals("example-repo", collaboratorDTO.getRepo());
        assertEquals("johnDoe", collaboratorDTO.getUsername());
        assertEquals("token123", collaboratorDTO.getAccessToken());
    }

    @Test
    void testGetterSetter() {
        CollaboratorDTO collaboratorDTO = new CollaboratorDTO();

        collaboratorDTO.setOwner("Alice");
        collaboratorDTO.setRepo("project-repo");
        collaboratorDTO.setUsername("alice");
        collaboratorDTO.setAccessToken("token456");

        assertEquals("Alice", collaboratorDTO.getOwner());
        assertEquals("project-repo", collaboratorDTO.getRepo());
        assertEquals("alice", collaboratorDTO.getUsername());
        assertEquals("token456", collaboratorDTO.getAccessToken());
    }


}
