package com.example.devopsproj.dto.responsedto;

import com.example.devopsproj.dto.responsedto.CollaboratorDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CollaboratorDTOTest {

    @Test
    void testConstructorAndGetters() {
        String owner = "OwnerName";
        String repo = "RepoName";
        String username = "Username";
        String accessToken = "AccessToken";

        CollaboratorDTO collaboratorDTO = new CollaboratorDTO(owner, repo, username, accessToken);

        assertEquals(owner, collaboratorDTO.getOwner());
        assertEquals(repo, collaboratorDTO.getRepo());
        assertEquals(username, collaboratorDTO.getUsername());
        assertEquals(accessToken, collaboratorDTO.getAccessToken());
    }

    @Test
    void testDefaultConstructor() {
        CollaboratorDTO collaboratorDTO = new CollaboratorDTO();

        assertNull(collaboratorDTO.getOwner());
        assertNull(collaboratorDTO.getRepo());
        assertNull(collaboratorDTO.getUsername());
        assertNull(collaboratorDTO.getAccessToken());
    }

    @Test
    void testSetters() {
        CollaboratorDTO collaboratorDTO = new CollaboratorDTO();
        String owner = "NewOwner";
        String repo = "NewRepo";
        String username = "NewUsername";
        String accessToken = "NewAccessToken";

        collaboratorDTO.setOwner(owner);
        collaboratorDTO.setRepo(repo);
        collaboratorDTO.setUsername(username);
        collaboratorDTO.setAccessToken(accessToken);

        assertEquals(owner, collaboratorDTO.getOwner());
        assertEquals(repo, collaboratorDTO.getRepo());
        assertEquals(username, collaboratorDTO.getUsername());
        assertEquals(accessToken, collaboratorDTO.getAccessToken());
    }

    @Test
    void testGeneratedMethods() {
        // Create an instance of CollaboratorDTO
        CollaboratorDTO collaboratorDTO = new CollaboratorDTO("OwnerName", "RepoName", "Username", "AccessToken");

        // Test getters
        assertEquals("OwnerName", collaboratorDTO.getOwner());
        assertEquals("RepoName", collaboratorDTO.getRepo());
        assertEquals("Username", collaboratorDTO.getUsername());
        assertEquals("AccessToken", collaboratorDTO.getAccessToken());
    }

    @Test
    void testConstructorWithEmptyValues() {
        CollaboratorDTO collaboratorDTO = new CollaboratorDTO("", "", "", "");
        assertEquals("", collaboratorDTO.getOwner());
        assertEquals("", collaboratorDTO.getRepo());
        assertEquals("", collaboratorDTO.getUsername());
        assertEquals("", collaboratorDTO.getAccessToken());
    }

    @Test
    void testConstructorWithNullValues() {
        CollaboratorDTO collaboratorDTO = new CollaboratorDTO(null, null, null, null);
        assertNull(collaboratorDTO.getOwner());
        assertNull(collaboratorDTO.getRepo());
        assertNull(collaboratorDTO.getUsername());
        assertNull(collaboratorDTO.getAccessToken());
    }

    @Test
    void testConstructorWithLongValues() {
        // Create very long strings for owner, repo, username, and accessToken
        String longOwner = "O".repeat(256);
        String longRepo = "R".repeat(256);
        String longUsername = "U".repeat(256);
        String longAccessToken = "A".repeat(1000);

        CollaboratorDTO collaboratorDTO = new CollaboratorDTO(longOwner, longRepo, longUsername, longAccessToken);

        assertEquals(longOwner, collaboratorDTO.getOwner());
        assertEquals(longRepo, collaboratorDTO.getRepo());
        assertEquals(longUsername, collaboratorDTO.getUsername());
        assertEquals(longAccessToken, collaboratorDTO.getAccessToken());
    }
}
