package com.example.devopsproj.service.implementations;

import com.example.devopsproj.dto.responseDto.GitRepositoryDTO;
import com.example.devopsproj.model.GitRepository;
import com.example.devopsproj.commons.enumerations.EnumRole;
import com.example.devopsproj.model.Project;
import com.example.devopsproj.repository.GitRepositoryRepository;
import com.example.devopsproj.repository.ProjectRepository;
import com.example.devopsproj.service.interfaces.GitRepositoryService;
import com.example.devopsproj.service.interfaces.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GitRepositoryServiceImpl implements GitRepositoryService {

    private static final String API_BASE_URL = "https://api.github.com";
    private static final String USER_REPOS_ENDPOINT = "/user/repos";
    private static final String REPOS_ENDPOINT = "/repos/Bindushree-0906";
    private static final String OWNER = "Bindushree-0906";

    private static final String GITHUB_ACCESS_TOKEN = System.getenv("GITHUB_ACCESS_TOKEN");

    private final RestTemplate restTemplate;
    private final GitRepositoryRepository gitRepositoryRepository;
    private final ProjectRepository projectRepository;
    private final ProjectService projectService;


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
            gitRepositoryRepository.save(createdGitRepository);
            return createdGitRepository;
        } else {
            throw new RuntimeException("Error creating repository " + gitRepository.getName() + ". Response: " + responseEntity.getBody());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<GitRepositoryDTO> getAllRepositories() {
        List<GitRepository> gitRepositories = gitRepositoryRepository.findAll();

        List<GitRepositoryDTO> gitRepositoriesDTO = gitRepositories.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return gitRepositoriesDTO;
    }

    @Override
    @Transactional
    public void deleteRepository(Long repoId) {
        GitRepository repository = gitRepositoryRepository.findByRepoId(repoId)
                .orElseThrow(() -> new RuntimeException("Repository not found with repoId: " + repoId));

        String repoName = repository.getName(); // Get the repository name from the entity

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(GITHUB_ACCESS_TOKEN);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Void> responseEntity = restTemplate.exchange(
                    API_BASE_URL + REPOS_ENDPOINT + "/" + repository.getName(), // Use the correct repository name
                    HttpMethod.DELETE,
                    requestEntity,
                    Void.class);

            if (responseEntity.getStatusCode() != HttpStatus.NO_CONTENT) {
                throw new RuntimeException("Error deleting repository with repoId " + repoId + ". Response: " + responseEntity.getBody());
            }

            gitRepositoryRepository.delete(repository);
        } catch (HttpClientErrorException.NotFound e) {
            throw new RuntimeException("Repository with repoId " + repoId + " not found.", e);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting repository with repoId " + repoId, e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<GitRepositoryDTO> getAllRepositoriesByProject(Long id) {
        Project project = projectService.getProjectById(id).orElse(null);
        if (project != null) {
            List<GitRepository> repositories = gitRepositoryRepository.findRepositoriesByProject(project);
            List<GitRepositoryDTO> gitRepositoryDTOS = repositories.stream()
                    .map(repository -> new GitRepositoryDTO(repository.getName(), repository.getDescription()))
                    .collect(Collectors.toList());
            return gitRepositoryDTOS;
        } else return null;
    }

    @Override
    @Transactional(readOnly = true)
    public List<GitRepositoryDTO> getAllReposByRole(EnumRole enumRole) {
        List<GitRepository> gitRepositories = gitRepositoryRepository.findAllByRole(enumRole);
        List<GitRepositoryDTO> gitRepositoryDTOS = gitRepositories.stream()
                .map(repository -> new GitRepositoryDTO(repository.getRepoId(), repository.getName(), repository.getDescription()))
                .collect(Collectors.toList());
        return gitRepositoryDTOS;
    }

    @Override
    @Transactional(readOnly = true)
    public GitRepository getRepositoryById(Long id) {
        return gitRepositoryRepository.findById(id).orElse(null);
    }

    @Override
    public boolean isAccessTokenValid(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<String> responseEntity = restTemplate.exchange(
                "https://api.github.com/user",
                HttpMethod.GET,
                requestEntity,
                String.class);

        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            String responseBody = responseEntity.getBody();
        }

        return false;
    }

    private GitRepositoryDTO convertToDto(GitRepository gitRepository) {
        // Perform the mapping logic from GitRepository to GitRepositoryDTO
        GitRepositoryDTO gitRepositoryDTO = new GitRepositoryDTO();
        gitRepositoryDTO.setRepoId(gitRepository.getRepoId());
        gitRepositoryDTO.setName(gitRepository.getName());
        gitRepositoryDTO.setDescription(gitRepository.getDescription());

        return gitRepositoryDTO;
    }
}
