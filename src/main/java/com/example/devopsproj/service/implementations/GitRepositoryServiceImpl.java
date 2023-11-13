package com.example.devopsproj.service.implementations;

import com.example.devopsproj.commons.enumerations.EnumRole;
import com.example.devopsproj.exceptions.NotFoundException;
import com.example.devopsproj.exceptions.RepositoryCreationException;
import com.example.devopsproj.exceptions.RepositoryDeletionException;
import com.example.devopsproj.model.GitRepository;
import com.example.devopsproj.dto.responsedto.GitRepositoryDTO;
import com.example.devopsproj.model.Project;
import com.example.devopsproj.repository.GitRepositoryRepository;
import com.example.devopsproj.service.interfaces.GitRepositoryService;
import java.util.Collections;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * The `GitRepositoryServiceImpl` class provides services for managing Git repositories, including
 * creating, deleting, and retrieving repositories. It also includes methods for listing repositories
 * based on a user's role and a specific project.
 *
 * @version 2.0
 */

@Service
@RequiredArgsConstructor
public class GitRepositoryServiceImpl implements GitRepositoryService {

    private final GitRepositoryRepository gitRepositoryRepository;
    private final ProjectServiceImpl projectServiceImpl;
    private final JdbcTemplate jdbcTemplate;

    private static final Logger logger = LoggerFactory.getLogger(GitRepositoryServiceImpl.class);

    private static final String API_BASE_URL = "https://api.github.com";
    private static final String USER_REPOS_ENDPOINT = "/user/repos";
    private static final String REPOS_ENDPOINT = "/repos/sahilanna";
    private static final String GITHUB_ACCESS_TOKEN = System.getenv("GITHUB_ACCESS_TOKEN");

    private final RestTemplate restTemplate;

    /**
     * Create Repository
     *
     * @param gitRepository The model for storing the repository members.
     * @return GitRepository to be created.
     */
    @Override
    @Transactional
    public GitRepository createRepository(GitRepository gitRepository) {
        logger.info("Creating repository: {}", gitRepository.getName());

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

            logger.info("Repository {} created successfully.", createdGitRepository.getName());

            return createdGitRepository;
        } else {
            logger.warn("Failed to create repository {}. Response: {}", gitRepository.getName(), responseEntity.getBody());
            throw new RepositoryCreationException("Error creating repository " + gitRepository.getName() + ". Response: " + responseEntity.getBody());
        }
    }

    /**
     * Get all repositories
     *
     * @return List of git repositories to be fetched using DTO
     */
    @Override
    @Transactional(readOnly = true)
    public List<GitRepositoryDTO> getAllRepositories() {
        logger.info("Fetching all repositories.");
        List<GitRepository> gitRepositories = gitRepositoryRepository.findAll();

        logger.info("Fetched {} repositories", gitRepositories.size());
        return gitRepositories.stream()
                .map(this::convertToDto)
                .toList();
    }

    /**
     * Delete repository
     *
     * @param repoId The ID of the repository that is to be deleted.
     */
    @Override
    @Transactional
    public void deleteRepository(Long repoId) {
        logger.info("Deleting repository with repoId: {}", repoId);

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
                logger.info("Repository with repoId {} deleted successfully.", repoId);
                throw new RepositoryDeletionException("Error deleting repository with repoId " + repoId + ". Response: " + responseEntity.getBody());
            }

            gitRepositoryRepository.delete(repository);
        } catch (Exception e) {
            logger.error("Error deleting repository with repoId {}: {}", repoId, e.getMessage());
            throw new RepositoryDeletionException("Error deleting repository with repoId " + repoId);
        }
    }

    /**
     * Get all repositories based on Project ID
     *
     * @param id The ID of project whose repositories are to be fetched.
     * @return List of git repos that are fetched using DTO.
     */
    @Override
    public List<GitRepositoryDTO> getAllRepositoriesByProject(Long id) {
        logger.info("Fetching repositories for project with ID: {}", id);

        Project project = projectServiceImpl.getProjectById(id).orElse(null);
        if(project != null){
            List<GitRepository> repositories = gitRepositoryRepository.findRepositoriesByProject(project);

            logger.info("Fetched {} repositories for project with ID: {}", repositories.size(), id);

            return repositories.stream()
                    .map(repository -> new GitRepositoryDTO(repository.getName(), repository.getDescription()))
                    .toList();
        }
        else {
            logger.warn("Project with ID {} not found.", id);
            return Collections.emptyList();
        }
    }

    /**
     * Get all repos by role
     *
     * @param enumRole The role based on which the repos are fetched.
     * @return List of git repos using DTO.
     */
    @Override
    public List<GitRepositoryDTO> getAllReposByRole(EnumRole enumRole) {
        logger.info("Fetching repositories for role: {}", enumRole);

        List<GitRepository> gitRepositories = gitRepositoryRepository.findAllByRole(enumRole);

        logger.info("Fetched {} repositories for role: {}", gitRepositories.size(), enumRole);

        return gitRepositories.stream()
                .map(repository -> new GitRepositoryDTO(repository.getRepoId(), repository.getName(), repository.getDescription()))
                .toList();
    }

    /**
     * Get repo by ID
     *
     * @param id The ID of the repository to be fetched.
     * @return GitRepository that is fetched.
     */
    @Override
    @Transactional(readOnly = true)
    public GitRepository getRepositoryById(Long id) {
        logger.info("Fetching repository by ID: {}", id);
        return gitRepositoryRepository.findById(id).orElse(null);
    }


    // ---- Other methods ------


    public GitRepositoryDTO convertToDto(GitRepository gitRepository) {
        // Perform the mapping logic from GitRepository to GitRepositoryDTO
        GitRepositoryDTO gitRepositoryDTO = new GitRepositoryDTO();
        gitRepositoryDTO.setRepoId(gitRepository.getRepoId());
        gitRepositoryDTO.setName(gitRepository.getName());
        gitRepositoryDTO.setDescription(gitRepository.getDescription());

        return gitRepositoryDTO;
    }
}
