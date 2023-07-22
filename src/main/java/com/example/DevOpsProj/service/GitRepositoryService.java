package com.example.DevOpsProj.service;

import com.example.DevOpsProj.commons.enumerations.EnumRole;
import com.example.DevOpsProj.dto.responseDto.GitRepositoryDTO;
import com.example.DevOpsProj.model.GitRepository;
import com.example.DevOpsProj.model.Project;
//import com.example.DevOpsProj.model.RepositoryEntity;
import com.example.DevOpsProj.repository.GitRepositoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GitRepositoryService {


    @Autowired
    private GitRepositoryRepository gitRepositoryRepository;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private ModelMapper modelMapper;

    @Value("${github.accessToken}")
    private String accessToken;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String API_BASE_URL = "https://api.github.com";
    private static final String USER_REPOS_ENDPOINT = "/user/repos";

    private static final String REPOS_ENDPOINT = "/repos/Bindushree-0906";


    private final RestTemplate restTemplate;

    public GitRepositoryService() {
        this.restTemplate = new RestTemplate();
        this.restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @Transactional
    public GitRepository createRepository(GitRepository gitRepository) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
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
    @Transactional(readOnly = true)
    public List<GitRepositoryDTO> getAllRepositories() {
        List<GitRepository> gitRepositories = gitRepositoryRepository.findAll();

        List<GitRepositoryDTO> gitRepositoriesDTO = gitRepositories.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        return gitRepositoriesDTO;
    }



    @Transactional
    public void deleteRepository(Long repoId) {
        GitRepository repository = gitRepositoryRepository.findByRepoId(repoId)
                .orElseThrow(() -> new RuntimeException("Repository not found with repoId: " + repoId));

        String repoName = repository.getName();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Void> responseEntity = restTemplate.exchange(
                    API_BASE_URL + REPOS_ENDPOINT + "/" + repoName,
                    HttpMethod.DELETE,
                    requestEntity,
                    Void.class);

            if (responseEntity.getStatusCode() != HttpStatus.NO_CONTENT) {
                throw new RuntimeException("Error deleting repository with repoId " + repoId + ". Response: " + responseEntity.getBody());
            }

            gitRepositoryRepository.delete(repository);
        } catch (HttpClientErrorException.NotFound e) {
            throw new RuntimeException("Repository with repoId " + repoId + " not found.", e);
        }
    }




    public List<GitRepositoryDTO> getAllRepositoriesByProject(Long id) {
        Project project = projectService.getProjectById(id).orElse(null);
        if(project != null){
            List<GitRepository> repositories = gitRepositoryRepository.findRepositoriesByProject(project);
            List<GitRepositoryDTO> gitRepositoryDTOS = repositories.stream()
                    .map(repository -> new GitRepositoryDTO(repository.getName(), repository.getDescription()))
                    .collect(Collectors.toList());
            return gitRepositoryDTOS;
        }
        else return null;
    }

    private GitRepositoryDTO convertToDto(GitRepository gitRepository) {
        // Perform the mapping logic from GitRepository to GitRepositoryDTO
        GitRepositoryDTO gitRepositoryDTO = new GitRepositoryDTO();
        gitRepositoryDTO.setRepoId(gitRepository.getRepoId());
        gitRepositoryDTO.setName(gitRepository.getName());
        gitRepositoryDTO.setDescription(gitRepository.getDescription());

        return gitRepositoryDTO;
    }

    public List<GitRepositoryDTO> getAllReposByRole(EnumRole enumRole) {
        List<GitRepository> gitRepositories = gitRepositoryRepository.findAllByRole(enumRole);
        List<GitRepositoryDTO> gitRepositoryDTOS = gitRepositories.stream()
                .map(repository -> new GitRepositoryDTO(repository.getRepoId(), repository.getName(), repository.getDescription()))
                .collect(Collectors.toList());
        return gitRepositoryDTOS;
    }

    @Transactional(readOnly = true)
    public GitRepository getRepositoryById(Long id) {
        return gitRepositoryRepository.findById(id).orElse(null);
    }

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
            // Check if the user has the required permissions to delete repositories
            String responseBody = responseEntity.getBody();
            // Parse the response and check the user's permissions
            // Here, you can check if the "delete_repo" permission is present or any other permissions as needed
            // If the user has the required permissions, return true
            // Otherwise, return false
        }

        return false;
    }
}
