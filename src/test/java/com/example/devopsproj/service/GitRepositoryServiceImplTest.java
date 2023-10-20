package com.example.devopsproj.service;

import com.example.devopsproj.commons.enumerations.EnumRole;
import com.example.devopsproj.dto.responsedto.GitRepositoryDTO;
import com.example.devopsproj.exceptions.NotFoundException;
import com.example.devopsproj.exceptions.RepositoryCreationException;
import com.example.devopsproj.exceptions.RepositoryDeletionException;
import com.example.devopsproj.model.GitRepository;
import com.example.devopsproj.model.Project;
import com.example.devopsproj.repository.GitRepositoryRepository;
import com.example.devopsproj.service.implementations.GitRepositoryServiceImpl;
import com.example.devopsproj.service.implementations.ProjectServiceImpl;
import com.example.devopsproj.service.interfaces.GitRepositoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GitRepositoryServiceImplTest {

    @InjectMocks
    private GitRepositoryServiceImpl gitRepositoryService;
    @Mock
    private GitRepositoryRepository gitRepositoryRepository;
    @Mock
    private ProjectServiceImpl projectService;
    @Mock
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    class CreateRepositoryTest {
        @Test
        @DisplayName("Testing success case for creating repo")
        void testCreateRepository_Successful() {
            GitRepository gitRepository = new GitRepository();
            gitRepository.setName("my-repo");

            ResponseEntity<GitRepository> successfulResponse = new ResponseEntity<>(gitRepository, HttpStatus.CREATED);
            when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(GitRepository.class)))
                    .thenReturn(successfulResponse);

            GitRepository result = gitRepositoryService.createRepository(gitRepository);

            assertNotNull(result);
            assertEquals(gitRepository.getName(), result.getName());
        }

        @Test
        @DisplayName("Testing failure case for creating repo")
        void testCreateRepository_Unsuccessful() {
            GitRepository gitRepository = new GitRepository();
            gitRepository.setName("my-repo");

            // Mock an HTTP error (Bad Request in this case)
            HttpClientErrorException httpClientErrorException = new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request");
            when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(GitRepository.class)))
                    .thenThrow(httpClientErrorException);

            assertThrows(HttpClientErrorException.class, () -> gitRepositoryService.createRepository(gitRepository));
        }

        @Test
        @DisplayName("Testing repository creation fail case")
        void testCreateRepositoryFailure() {
            ResponseEntity<GitRepository> failedResponse = new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

            when(restTemplate.exchange(
                    anyString(),
                    any(HttpMethod.class),
                    any(HttpEntity.class),
                    eq(GitRepository.class))
            ).thenReturn(failedResponse);

            GitRepository gitRepository = new GitRepository();
            gitRepository.setName("test-repo");

            try {
                gitRepositoryService.createRepository(gitRepository);
            } catch (RepositoryCreationException ex) {
                assertTrue(ex.getMessage().contains("Error creating repository test-repo"));
            }
        }
    }

    @Nested
    class DeleteRepositoryTest {
        @Test
        @DisplayName("Testing success case for deleting repo")
        void testDeleteRepository_Successful() {
            Long repoId = 1L;
            GitRepository repository = new GitRepository();
            repository.setRepoId(repoId);
            repository.setName("my-repo");

            when(gitRepositoryRepository.findByRepoId(repoId)).thenReturn(Optional.of(repository));
            when(restTemplate.exchange(anyString(), eq(HttpMethod.DELETE), any(HttpEntity.class), eq(Void.class)))
                    .thenReturn(new ResponseEntity<>(HttpStatus.NO_CONTENT));

            assertDoesNotThrow(() -> gitRepositoryService.deleteRepository(repoId));

            verify(gitRepositoryRepository, times(1)).delete(repository);
        }

        @Test
        @DisplayName("Testing no content case")
        void testDeleteRepository_Successful_NoContent() {
            Long repoId = 1L;
            GitRepository repository = new GitRepository();
            repository.setRepoId(repoId);
            repository.setName("my-repo");

            when(gitRepositoryRepository.findByRepoId(repoId)).thenReturn(Optional.of(repository));
            doNothing().when(gitRepositoryRepository).delete(repository);

            ResponseEntity<Void> noContentResponse = new ResponseEntity<>(HttpStatus.NO_CONTENT);
            doReturn(noContentResponse).when(restTemplate).exchange(anyString(), eq(HttpMethod.DELETE), any(HttpEntity.class), eq(Void.class));

            gitRepositoryService.deleteRepository(repoId);

            verify(gitRepositoryRepository).delete(repository);
        }

        @Test
        @DisplayName("Testing failure case for deleting repo")
        void testDeleteRepository_Unsuccessful() {
            Long repoId = 1L;
            GitRepository repository = new GitRepository();
            repository.setRepoId(repoId);
            repository.setName("my-repo");

            when(gitRepositoryRepository.findByRepoId(repoId)).thenReturn(Optional.of(repository));

            HttpClientErrorException httpClientErrorException = new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request");

            doThrow(httpClientErrorException)
                    .when(restTemplate).exchange(anyString(), eq(HttpMethod.DELETE), any(HttpEntity.class), eq(Void.class));

            RepositoryDeletionException exception = assertThrows(RepositoryDeletionException.class, () -> gitRepositoryService.deleteRepository(repoId));

            assertTrue(exception.getMessage().contains("Error deleting repository with repoId " + repoId));
        }

        @Test
        @DisplayName("Testing not found case")
        void testDeleteRepository_NotFound() {
            Long repoId = 1L;

            when(gitRepositoryRepository.findByRepoId(repoId)).thenReturn(Optional.empty());

            NotFoundException exception = assertThrows(NotFoundException.class, () -> gitRepositoryService.deleteRepository(repoId));

            assertTrue(exception.getMessage().contains("Repository with repoId " + repoId + " not found."));
        }
    }

    @Nested
    class GetAllRepositoriesTest {
        @Test
        @DisplayName("Testing success case for repos list")
        void testGetAllRepositories_Success() {
            GitRepository repo1 = new GitRepository();
            repo1.setRepoId(1L);
            repo1.setName("Repo1");
            repo1.setDescription("Repo1 Description");

            GitRepository repo2 = new GitRepository();
            repo2.setRepoId(2L);
            repo2.setName("Repo2");
            repo2.setDescription("Repo2 Description");

            List<GitRepository> mockRepositoryList = Arrays.asList(repo1, repo2);

            when(gitRepositoryRepository.findAll()).thenReturn(mockRepositoryList);

            List<GitRepositoryDTO> result = gitRepositoryService.getAllRepositories();

            assertAll(
                    () -> assertEquals(2, result.size(), "Number of repositories should be 2"),
                    () -> assertEquals("Repo1", result.get(0).getName(), "First repository name should be 'Repo1'"),
                    () -> assertEquals("Repo1 Description", result.get(0).getDescription(), "First repository description should be 'Repo1 Description'"),
                    () -> assertEquals("Repo2", result.get(1).getName(), "Second repository name should be 'Repo2'"),
                    () -> assertEquals("Repo2 Description", result.get(1).getDescription(), "Second repository description should be 'Repo2 Description'")
            );
        }

        @Test
        @DisplayName("Testing failure case - empty list")
        void testGetAllRepositories_EmptyList() {
            when(gitRepositoryRepository.findAll()).thenReturn(List.of());

            List<GitRepositoryDTO> result = gitRepositoryService.getAllRepositories();

            assertEquals(0, result.size());
        }
    }

    @Nested
    class GetAllRepositoriesByProjectTest {
        @Test
        @DisplayName("Testing success case for repos list")
        void testGetAllRepositoriesByProject_Success() {
            Long projectId = 1L;
            Project project = new Project();
            project.setProjectId(projectId);
            project.setProjectName("P1");
            project.setProjectDescription("P1 Description");

            GitRepository repo1 = new GitRepository();
            repo1.setRepoId(1L);
            repo1.setName("Repo1");
            repo1.setDescription("Repo1 Description");

            GitRepository repo2 = new GitRepository();
            repo2.setRepoId(2L);
            repo2.setName("Repo2");
            repo2.setDescription("Repo2 Description");

            List<GitRepository> mockRepositoryList = List.of(repo1, repo2);

            when(projectService.getProjectById(projectId)).thenReturn(Optional.of(project));
            when(gitRepositoryRepository.findRepositoriesByProject(project)).thenReturn(mockRepositoryList);

            List<GitRepositoryDTO> result = gitRepositoryService.getAllRepositoriesByProject(projectId);

            assertAll(
                    () -> assertEquals(2, result.size(), "Number of repositories should be 2"),
                    () -> assertEquals("Repo1", result.get(0).getName(), "First repository name should be 'Repo1'"),
                    () -> assertEquals("Repo1 Description", result.get(0).getDescription(), "First repository description should be 'Repo1 Description'"),
                    () -> assertEquals("Repo2", result.get(1).getName(), "Second repository name should be 'Repo2'"),
                    () -> assertEquals("Repo2 Description", result.get(1).getDescription(), "Second repository description should be 'Repo2 Description'")
            );
        }


        @Test
        @DisplayName("Testing failure case - Project not found")
        void testGetAllRepositoriesByProject_NoProjectFound() {
            Long projectId = 1L;

            when(projectService.getProjectById(projectId)).thenReturn(Optional.empty());

            List<GitRepositoryDTO> result = gitRepositoryService.getAllRepositoriesByProject(projectId);

            assertEquals(0, result.size());
        }

        @Test
        @DisplayName("Testing empty repo list case")
        void testGetAllRepositoriesByProject_EmptyRepositoryList() {
            Long projectId = 1L;
            Project project = new Project();
            project.setProjectId(projectId);
            project.setProjectName("P1");
            project.setProjectDescription("P1 Description");

            when(projectService.getProjectById(projectId)).thenReturn(Optional.of(project));
            when(gitRepositoryRepository.findRepositoriesByProject(project)).thenReturn(Collections.emptyList());

            List<GitRepositoryDTO> result = gitRepositoryService.getAllRepositoriesByProject(projectId);

            assertEquals(0, result.size());
        }
    }

    @Nested
    class GetAllReposByRoleTest {
        @Test
        @DisplayName("Testing success case for getting repos list")
        void testGetAllReposByRole_Success() {
            EnumRole role = EnumRole.ADMIN;
            GitRepository repo1 = new GitRepository();
            repo1.setRepoId(1L);
            repo1.setName("Repo1");
            repo1.setDescription("Repo1 Description");

            GitRepository repo2 = new GitRepository();
            repo2.setRepoId(2L);
            repo2.setName("Repo2");
            repo2.setDescription("Repo2 Description");

            List<GitRepository> mockRepositoryList = List.of(repo1, repo2);

            when(gitRepositoryRepository.findAllByRole(role)).thenReturn(mockRepositoryList);

            List<GitRepositoryDTO> result = gitRepositoryService.getAllReposByRole(role);

            assertAll(
                    () -> assertEquals(2, result.size(), "Number of repositories should be 2"),
                    () -> assertEquals(1L, result.get(0).getRepoId(), "First repository id should be 1L"),
                    () -> assertEquals("Repo1", result.get(0).getName(), "First repository name should be 'Repo1'"),
                    () -> assertEquals("Repo1 Description", result.get(0).getDescription(), "First repository description should be 'Repo1 Description'"),
                    () -> assertEquals(2L, result.get(1).getRepoId(), "Second repository id should be 2L"),
                    () -> assertEquals("Repo2", result.get(1).getName(), "Second repository name should be 'Repo2'"),
                    () -> assertEquals("Repo2 Description", result.get(1).getDescription(), "Second repository description should be 'Repo2 Description'")
            );
        }


        @Test
        @DisplayName("Testing failure case - no repos found")
        void testGetAllReposByRole_NoReposFound() {
            EnumRole role = EnumRole.ADMIN;

            when(gitRepositoryRepository.findAllByRole(role)).thenReturn(Collections.emptyList());

            List<GitRepositoryDTO> result = gitRepositoryService.getAllReposByRole(role);

            assertEquals(0, result.size());
        }
    }

    @Nested
    class GetRepositoryByIdTest {
        @Test
        @DisplayName("Testing success case - repo exists")
        void testGetRepositoryById_RepositoryExists() {
            Long repositoryId = 1L;
            GitRepository expectedRepository = new GitRepository();
            expectedRepository.setRepoId(1L);
            expectedRepository.setName("Repo1");
            expectedRepository.setDescription("Repo1 Description");

            when(gitRepositoryRepository.findById(repositoryId)).thenReturn(Optional.of(expectedRepository));

            GitRepository result = gitRepositoryService.getRepositoryById(repositoryId);

            assertEquals(expectedRepository, result);
        }

        @Test
        @DisplayName("Testing failure case - repo does not exist")
        void testGetRepositoryById_RepositoryDoesNotExist() {
            Long repositoryId = 2L;

            when(gitRepositoryRepository.findById(repositoryId)).thenReturn(Optional.empty());

            GitRepository result = gitRepositoryService.getRepositoryById(repositoryId);

            assertNull(result);
        }
    }

    @Test
    void testConvertToDto() {
        GitRepository gitRepository = new GitRepository();
        gitRepository.setRepoId(1L);
        gitRepository.setName("Repo1");
        gitRepository.setDescription("Repo1 Description");

        GitRepositoryDTO gitRepositoryDTO = gitRepositoryService.convertToDto(gitRepository);

        assertAll("GitRepository to GitRepositoryDTO conversion",
                () -> assertNotNull(gitRepositoryDTO, "GitRepositoryDTO should not be null"),
                () -> assertEquals(gitRepository.getRepoId(), gitRepositoryDTO.getRepoId(), "RepoId should match"),
                () -> assertEquals(gitRepository.getName(), gitRepositoryDTO.getName(), "Name should match"),
                () -> assertEquals(gitRepository.getDescription(), gitRepositoryDTO.getDescription(), "Description should match")
        );
    }

}