package com.example.devopsproj.service.implementations;

import com.example.devopsproj.dto.responsedto.GitRepositoryDTO;


import com.example.devopsproj.dto.responsedto.ProjectDTO;
import com.example.devopsproj.exceptions.FigmaNotFoundException;
import com.example.devopsproj.exceptions.NotFoundException;
import com.example.devopsproj.exceptions.RepositoryCreationException;
import com.example.devopsproj.exceptions.RepositoryDeletionException;
import com.example.devopsproj.model.GitRepository;
import com.example.devopsproj.commons.enumerations.EnumRole;

import com.example.devopsproj.model.Project;
import com.example.devopsproj.repository.GitRepositoryRepository;

import com.example.devopsproj.service.interfaces.GitRepositoryService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.web.client.RestTemplate;


import java.util.Collections;
import java.util.List;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;

@Service
@RequiredArgsConstructor
public class GitRepositoryServiceImpl implements GitRepositoryService {

    private final GitRepositoryRepository gitRepositoryRepository;
    private final ProjectServiceImpl projectServiceImpl;
    private final JdbcTemplate jdbcTemplate;
    private final RestTemplate restTemplate;

    private static final String API_BASE_URL = "https://api.github.com";
    private static final String USER_REPOS_ENDPOINT = "/user/repos";
    private static final String REPOS_ENDPOINT = "/repos/sahilanna";
    private static final String GITHUB_ACCESS_TOKEN = System.getenv("GITHUB_ACCESS_TOKEN");

    // Create a logger for the class
    private static final Logger logger = LoggerFactory.getLogger(GitRepositoryServiceImpl.class);

    @Override
    @Transactional
    public GitRepository createRepository(GitRepository gitRepository) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(GITHUB_ACCESS_TOKEN);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<GitRepository> requestEntity = new HttpEntity<>(gitRepository, headers);

        ResponseEntity<GitRepository> responseEntity = restTemplate.exchange(
                API_BASE_URL + USER_REPOS_ENDPOINT,
                HttpMethod.POST,
                requestEntity,
                GitRepository.class);

        if (responseEntity.getStatusCode() == HttpStatus.CREATED) {
            GitRepository createdGitRepository = responseEntity.getBody();
            assert createdGitRepository != null;
            gitRepositoryRepository.save(createdGitRepository);
            return createdGitRepository;
        } else {
            throw new RepositoryCreationException("Error creating repository " + gitRepository.getName() + ". Response: " + responseEntity.getBody());
        }
    }






    // Get a list of all Git repositories
    @Override
    @Transactional(readOnly = true)
    public List<GitRepositoryDTO> getAllRepositories() {
        // Retrieve all Git repositories from the local repository
        List<GitRepository> gitRepositories = gitRepositoryRepository.findAll();

        // Convert the GitRepository entities to GitRepositoryDTOs and return the result directly
        return gitRepositories.stream()
                .map(this::convertToDto)
                .toList(); // Use Stream.toList() for simplicity
    }


    // Delete a Git repository by its ID
    @Override
    @Transactional
    public void deleteRepository(Long repoId) {
        GitRepository repository = gitRepositoryRepository.findByRepoId(repoId)
                .orElseThrow(() -> new NotFoundException("Repository with repoId " + repoId + " not found."));

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(GITHUB_ACCESS_TOKEN);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Void> responseEntity = restTemplate.exchange(
                    API_BASE_URL + REPOS_ENDPOINT + "/" + repository.getName(),
                    HttpMethod.DELETE,
                    requestEntity,
                    Void.class);

            if (responseEntity.getStatusCode() != HttpStatus.NO_CONTENT) {
                throw new RepositoryDeletionException("Error deleting repository with repoId " + repoId + ". Response: " + responseEntity.getBody());
            }

            gitRepositoryRepository.delete(repository);
        } catch (Exception e) {
            throw new RepositoryDeletionException("Error deleting repository with repoId " + repoId);
        }
    }


    @Override
    public List<GitRepositoryDTO> getAllRepositoriesByProject(Long id) {
        ProjectDTO project = projectServiceImpl.getProjectById(id);
        if (project != null) {
            List<GitRepository> repositories = gitRepositoryRepository.findRepositoriesByProject(mapProjectDTOToProject(project));
            return repositories.stream()
                    .map(repository -> new GitRepositoryDTO(repository.getName(), repository.getDescription()))
                    .toList();
        } else {
            return Collections.emptyList();
        }
    }
    // Get a list of Git repositories by role
    @Override
    @Transactional(readOnly = true)
    public List<GitRepositoryDTO> getAllReposByRole(EnumRole enumRole) {
        // Retrieve Git repositories by role from the local repository
        List<GitRepository> gitRepositories = gitRepositoryRepository.findAllByRole(enumRole);

        // Convert the GitRepository entities to GitRepositoryDTOs and return the result directly
        return gitRepositories.stream()
                .map(repository -> new GitRepositoryDTO(repository.getRepoId(), repository.getName(), repository.getDescription()))
                .toList(); // Use Stream.toList() for simplicity
    }


    // Get a Git repository by its ID
    @Override
    @Transactional(readOnly = true)
    public GitRepository getRepositoryById(Long id) {
        try {
            // Retrieve the Git repository by its ID from the local repository
            return gitRepositoryRepository.findById(id).orElse(null);
        } catch (Exception e) {
            // Handle exceptions here if needed
            throw new FigmaNotFoundException("An error occurred while retrieving the Git repository by ID");
        }
    }


    // Helper method to convert a GitRepository entity to a GitRepositoryDTO
    private GitRepositoryDTO convertToDto(GitRepository gitRepository) {
        GitRepositoryDTO gitRepositoryDTO = new GitRepositoryDTO();
        gitRepositoryDTO.setRepoId(gitRepository.getRepoId());
        gitRepositoryDTO.setName(gitRepository.getName());
        gitRepositoryDTO.setDescription(gitRepository.getDescription());
        return gitRepositoryDTO;
    }

    public Project mapProjectDTOToProject(ProjectDTO projectDTO) {
        Project project = new Project();
        project.setProjectId(projectDTO.getProjectId());
        project.setProjectName(projectDTO.getProjectName());
        return project;
    }
}