package com.example.devopsproj.service;

import com.example.devopsproj.commons.enumerations.EnumRole;
import com.example.devopsproj.dto.responsedto.GitRepositoryDTO;
import com.example.devopsproj.model.GitRepository;
import com.example.devopsproj.model.Project;
import com.example.devopsproj.repository.GitRepositoryRepository;
import com.example.devopsproj.service.implementations.GitRepositoryServiceImpl;
import com.example.devopsproj.service.implementations.ProjectServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ----- SUCCESS -----

    @Test
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

        assertEquals(2, result.size());
        assertEquals("Repo1", result.get(0).getName());
        assertEquals("Repo1 Description", result.get(0).getDescription());
        assertEquals("Repo2", result.get(1).getName());
        assertEquals("Repo2 Description", result.get(1).getDescription());
    }

    @Test
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

        assertEquals(2, result.size());
        assertEquals("Repo1", result.get(0).getName());
        assertEquals("Repo1 Description", result.get(0).getDescription());
        assertEquals("Repo2", result.get(1).getName());
        assertEquals("Repo2 Description", result.get(1).getDescription());
    }

    @Test
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

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getRepoId());
        assertEquals("Repo1", result.get(0).getName());
        assertEquals("Repo1 Description", result.get(0).getDescription());
        assertEquals(2L, result.get(1).getRepoId());
        assertEquals("Repo2", result.get(1).getName());
        assertEquals("Repo2 Description", result.get(1).getDescription());
    }

    @Test
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
    void testConvertToDto() {
        GitRepository gitRepository = new GitRepository();
        gitRepository.setRepoId(1L);
        gitRepository.setName("Repo1");
        gitRepository.setDescription("Repo1 Description");

        GitRepositoryDTO gitRepositoryDTO = gitRepositoryService.convertToDto(gitRepository);

        assertNotNull(gitRepositoryDTO);
        assertEquals(gitRepository.getRepoId(), gitRepositoryDTO.getRepoId());
        assertEquals(gitRepository.getName(), gitRepositoryDTO.getName());
        assertEquals(gitRepository.getDescription(), gitRepositoryDTO.getDescription());
    }

    // ----- FAILURE -----

    @Test
    void testGetAllRepositories_EmptyList() {
        when(gitRepositoryRepository.findAll()).thenReturn(List.of());

        List<GitRepositoryDTO> result = gitRepositoryService.getAllRepositories();

        assertEquals(0, result.size());
    }

    @Test
    void testGetAllRepositoriesByProject_NoProjectFound() {
        Long projectId = 1L;

        when(projectService.getProjectById(projectId)).thenReturn(Optional.empty());

        List<GitRepositoryDTO> result = gitRepositoryService.getAllRepositoriesByProject(projectId);

        assertEquals(0, result.size());
    }

    @Test
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

    @Test
    void testGetAllReposByRole_NoReposFound() {
        EnumRole role = EnumRole.ADMIN;

        when(gitRepositoryRepository.findAllByRole(role)).thenReturn(Collections.emptyList());

        List<GitRepositoryDTO> result = gitRepositoryService.getAllReposByRole(role);

        assertEquals(0, result.size());
    }

    @Test
    void testGetRepositoryById_RepositoryDoesNotExist() {
        Long repositoryId = 2L;

        when(gitRepositoryRepository.findById(repositoryId)).thenReturn(Optional.empty());

        GitRepository result = gitRepositoryService.getRepositoryById(repositoryId);

        assertNull(result);
    }

}
