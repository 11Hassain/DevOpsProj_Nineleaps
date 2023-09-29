package com.example.devopsproj.controller;

import com.example.devopsproj.commons.enumerations.EnumRole;
import com.example.devopsproj.dto.responsedto.GitRepositoryDTO;
import com.example.devopsproj.model.GitRepository;
import com.example.devopsproj.service.implementations.GitRepositoryServiceImpl;
import com.example.devopsproj.service.implementations.JwtServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

class GitRepositoryControllerTest {

    @InjectMocks
    private GitRepositoryController gitRepositoryController;
    @Mock
    private GitRepositoryServiceImpl gitRepositoryService;
    @Mock
    private JwtServiceImpl jwtService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ----- SUCCESS (For VALID TOKEN) -----

    @Test
    void testCreateRepository_ValidToken(){
        GitRepository gitRepository = new GitRepository();

        when(jwtService.isTokenTrue(anyString())).thenReturn(true);
        when(gitRepositoryService.createRepository(gitRepository)).thenReturn(gitRepository);

        ResponseEntity<Object> response = gitRepositoryController.createRepository(gitRepository, "valid-access-token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(gitRepository, response.getBody());
    }

    @Test
    void testGetAllRepositories_ValidToken(){
        GitRepositoryDTO g1 = new GitRepositoryDTO();
        GitRepositoryDTO g2 = new GitRepositoryDTO();

        List<GitRepositoryDTO> gitRepositoryDTOList = new ArrayList<>();
        gitRepositoryDTOList.add(g1);
        gitRepositoryDTOList.add(g2);

        when(jwtService.isTokenTrue(anyString())).thenReturn(true);
        when(gitRepositoryService.getAllRepositories()).thenReturn(gitRepositoryDTOList);

        ResponseEntity<Object> response = gitRepositoryController.getAllRepositories("valid-access-token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(gitRepositoryDTOList, response.getBody());
    }

    @Test
    void testGetAllReposByProject_ValidToken(){
        Long projectId = 1L;

        GitRepositoryDTO g1 = new GitRepositoryDTO();
        GitRepositoryDTO g2 = new GitRepositoryDTO();

        List<GitRepositoryDTO> gitRepositoryDTOList = new ArrayList<>();
        gitRepositoryDTOList.add(g1);
        gitRepositoryDTOList.add(g2);

        when(jwtService.isTokenTrue(anyString())).thenReturn(true);
        when(gitRepositoryService.getAllRepositoriesByProject(projectId)).thenReturn(gitRepositoryDTOList);

        ResponseEntity<Object> response = gitRepositoryController.getAllReposByProject(projectId, "valid-access-token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(gitRepositoryDTOList, response.getBody());
    }

    @Test
    void testGetAllReposByRole_ValidToken(){
        String role = EnumRole.PROJECT_MANAGER.getEnumRole();

        GitRepositoryDTO g1 = new GitRepositoryDTO();
        GitRepositoryDTO g2 = new GitRepositoryDTO();

        List<GitRepositoryDTO> gitRepositoryDTOList = new ArrayList<>();
        gitRepositoryDTOList.add(g1);
        gitRepositoryDTOList.add(g2);

        when(jwtService.isTokenTrue(anyString())).thenReturn(true);
        when(gitRepositoryService.getAllReposByRole(EnumRole.PROJECT_MANAGER)).thenReturn(gitRepositoryDTOList);

        ResponseEntity<Object> response = gitRepositoryController.getAllReposByRole(role, "valid-access-token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(gitRepositoryDTOList, response.getBody());
    }

    @Test
    void testGetRepositoryById_ValidToken_RepoFound(){
        Long repoId = 1L;

        GitRepository gRepo = new GitRepository();
        gRepo.setRepoId(repoId);
        gRepo.setName("R1");
        gRepo.setDescription("R1 Description");

        when(jwtService.isTokenTrue(anyString())).thenReturn(true);
        when(gitRepositoryService.getRepositoryById(repoId)).thenReturn(gRepo);

        ResponseEntity<Object> response = gitRepositoryController.getRepositoryById(repoId, "valid-access-token");

        assertEquals(HttpStatus.OK, response.getStatusCode());

        // Check that the response body is a GitRepositoryDTO
        assertTrue(response.getBody() instanceof GitRepositoryDTO);

        GitRepositoryDTO repositoryDTO = (GitRepositoryDTO) response.getBody();

        assertEquals(gRepo.getName(), repositoryDTO.getName());
        assertEquals(gRepo.getDescription(), repositoryDTO.getDescription());
    }

    @Test
    void testGetRepositoryById_ValidToken_RepoNotFound(){
        Long repoId = 1L;

        when(jwtService.isTokenTrue(anyString())).thenReturn(true);
        when(gitRepositoryService.getRepositoryById(repoId)).thenReturn(null);

        ResponseEntity<Object> response = gitRepositoryController.getRepositoryById(repoId, "valid-access-token");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testDeleteRepository_ValidToken_Success(){
        Long repoId = 1L;

        when(jwtService.isTokenTrue(anyString())).thenReturn(true);

        ResponseEntity<Object> response = gitRepositoryController.deleteRepository(repoId, "valid-access-token");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Deleted successfully", response.getBody());
    }

    @Test
    void testDeleteRepository_ValidToken_Failed(){
        Long repoId = 1L;

        when(jwtService.isTokenTrue(anyString())).thenReturn(true);

        // Mock an exception when trying to delete the repository
        doThrow(new RuntimeException("Deletion error")).when(gitRepositoryService).deleteRepository(repoId);

        ResponseEntity<Object> response = gitRepositoryController.deleteRepository(repoId, "valid-access-token");

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Error", response.getBody());
    }

    // ----- FAILURE (For INVALID TOKEN) ------

    @Test
    void testCreateRepository_InvalidToken(){
        GitRepository gitRepository = new GitRepository();

        when(jwtService.isTokenTrue(anyString())).thenReturn(false);

        ResponseEntity<Object> response = gitRepositoryController.createRepository(gitRepository,"invalid-access-token");

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid Token", response.getBody());
    }

    @Test
    void testGetAllRepositories_InvalidToken(){
        when(jwtService.isTokenTrue(anyString())).thenReturn(false);

        ResponseEntity<Object> response = gitRepositoryController.getAllRepositories("invalid-access-token");

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid Token", response.getBody());
    }

    @Test
    void testGetAllReposByProject_InvalidToken(){
        Long repoId = 1L;

        when(jwtService.isTokenTrue(anyString())).thenReturn(false);

        ResponseEntity<Object> response = gitRepositoryController.getAllReposByProject(repoId, "invalid-access-token");

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid Token", response.getBody());
    }

    @Test
    void testGetAllReposByRole_InvalidToken(){
        String role = EnumRole.USER.getEnumRole();

        when(jwtService.isTokenTrue(anyString())).thenReturn(false);

        ResponseEntity<Object> response = gitRepositoryController.getAllReposByRole(role, "invalid-access-token");

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid Token", response.getBody());
    }

    @Test
    void testGetRepositoryById_InvalidToken(){
        Long repoId = 1L;

        when(jwtService.isTokenTrue(anyString())).thenReturn(false);

        ResponseEntity<Object> response = gitRepositoryController.getRepositoryById(repoId, "invalid-access-token");

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid Token", response.getBody());
    }

    @Test
    void testDeleteRepository_InvalidToken(){
        Long repoId = 1L;

        when(jwtService.isTokenTrue(anyString())).thenReturn(false);

        ResponseEntity<Object> response = gitRepositoryController.deleteRepository(repoId, "invalid-access-token");

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid Token", response.getBody());
    }
}
