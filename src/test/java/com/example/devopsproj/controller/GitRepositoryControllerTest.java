package com.example.devopsproj.controller;

import com.example.devopsproj.commons.enumerations.EnumRole;
import com.example.devopsproj.dto.responsedto.GitRepositoryDTO;
import com.example.devopsproj.model.GitRepository;
import com.example.devopsproj.service.interfaces.GitRepositoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

class GitRepositoryControllerTest {

    @InjectMocks
    private GitRepositoryController gitRepositoryController;
    @Mock
    private GitRepositoryService gitRepositoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    class CreateRepositoryTest {
        @Test
        @DisplayName("Testing success case with valid token")
        void testCreateRepository_ValidToken(){
            GitRepository gitRepository = new GitRepository();

            when(gitRepositoryService.createRepository(gitRepository)).thenReturn(gitRepository);

            ResponseEntity<Object> response = gitRepositoryController.createRepository(gitRepository);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(gitRepository, response.getBody());
        }
    }

    @Nested
    class GetAllRepositoriesTest {
        @Test
        @DisplayName("Testing success case with valid token")
        void testGetAllRepositories_ValidToken(){
            GitRepositoryDTO g1 = new GitRepositoryDTO();
            GitRepositoryDTO g2 = new GitRepositoryDTO();

            List<GitRepositoryDTO> gitRepositoryDTOList = new ArrayList<>();
            gitRepositoryDTOList.add(g1);
            gitRepositoryDTOList.add(g2);

            when(gitRepositoryService.getAllRepositories()).thenReturn(gitRepositoryDTOList);

            ResponseEntity<Object> response = gitRepositoryController.getAllRepositories();

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(gitRepositoryDTOList, response.getBody());
        }
    }

    @Nested
    class GetAllReposByProjectTest {
        @Test
        @DisplayName("Testing success case with valid token")
        void testGetAllReposByProject_ValidToken(){
            Long projectId = 1L;

            GitRepositoryDTO g1 = new GitRepositoryDTO();
            GitRepositoryDTO g2 = new GitRepositoryDTO();

            List<GitRepositoryDTO> gitRepositoryDTOList = new ArrayList<>();
            gitRepositoryDTOList.add(g1);
            gitRepositoryDTOList.add(g2);

            when(gitRepositoryService.getAllRepositoriesByProject(projectId)).thenReturn(gitRepositoryDTOList);

            ResponseEntity<Object> response = gitRepositoryController.getAllReposByProject(projectId);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(gitRepositoryDTOList, response.getBody());
        }
    }

    @Nested
    class GetAllReposByRoleTest {
        @Test
        @DisplayName("Testing success case with valid token")
        void testGetAllReposByRole_ValidToken(){
            String role = EnumRole.PROJECT_MANAGER.getEnumRole();

            GitRepositoryDTO g1 = new GitRepositoryDTO();
            GitRepositoryDTO g2 = new GitRepositoryDTO();

            List<GitRepositoryDTO> gitRepositoryDTOList = new ArrayList<>();
            gitRepositoryDTOList.add(g1);
            gitRepositoryDTOList.add(g2);

            when(gitRepositoryService.getAllReposByRole(EnumRole.PROJECT_MANAGER)).thenReturn(gitRepositoryDTOList);

            ResponseEntity<Object> response = gitRepositoryController.getAllReposByRole(role);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(gitRepositoryDTOList, response.getBody());
        }
    }

    @Nested
    class GetRepositoryByIdTest {
        @Test
        @DisplayName("Testing success case with valid token")
        void testGetRepositoryById_ValidToken_RepoFound(){
            Long repoId = 1L;

            GitRepository gRepo = new GitRepository();
            gRepo.setRepoId(repoId);
            gRepo.setName("R1");
            gRepo.setDescription("R1 Description");

            when(gitRepositoryService.getRepositoryById(repoId)).thenReturn(gRepo);

            ResponseEntity<Object> response = gitRepositoryController.getRepositoryById(repoId);

            assertEquals(HttpStatus.OK, response.getStatusCode());

            // Check that the response body is a GitRepositoryDTO
            assertTrue(response.getBody() instanceof GitRepositoryDTO);

            GitRepositoryDTO repositoryDTO = (GitRepositoryDTO) response.getBody();

            assertEquals(gRepo.getName(), repositoryDTO.getName());
            assertEquals(gRepo.getDescription(), repositoryDTO.getDescription());
        }

        @Test
        @DisplayName("Testing repo not found case")
        void testGetRepositoryById_ValidToken_RepoNotFound(){
            Long repoId = 1L;

            when(gitRepositoryService.getRepositoryById(repoId)).thenReturn(null);

            ResponseEntity<Object> response = gitRepositoryController.getRepositoryById(repoId);

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        }
    }

    @Nested
    class DeleteRepositoryTest {
        @Test
        @DisplayName("Testing success case with valid token")
        void testDeleteRepository_ValidToken_Success(){
            Long repoId = 1L;

            ResponseEntity<Object> response = gitRepositoryController.deleteRepository(repoId);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("Deleted successfully", response.getBody());
        }

        @Test
        @DisplayName("Testing failure case (Internal server error)")
        void testDeleteRepository_ValidToken_Failed(){
            Long repoId = 1L;

            // Mock an exception when trying to delete the repository
            doThrow(new RuntimeException("Deletion error")).when(gitRepositoryService).deleteRepository(repoId);

            ResponseEntity<Object> response = gitRepositoryController.deleteRepository(repoId);

            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
            assertEquals("Error", response.getBody());
        }
    }
}
