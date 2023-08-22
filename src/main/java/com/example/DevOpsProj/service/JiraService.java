package com.example.DevOpsProj.service;

import com.example.DevOpsProj.config.JiraConfig;
import com.example.DevOpsProj.dto.responseDto.JiraDTO;
import com.example.DevOpsProj.model.Jira;
import com.example.DevOpsProj.model.Project;
import com.example.DevOpsProj.repository.JiraRepository;
import com.example.DevOpsProj.repository.ProjectRepository;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.*;


@Service
public class JiraService {
    private final JiraConfig jiraConfig;
    private final RestTemplate restTemplate;
    private final JiraRepository jiraRepository;

    private final ProjectRepository projectRepository;

    public JiraService(JiraConfig jiraConfig, RestTemplate restTemplate, JiraRepository jiraRepository, ProjectRepository projectRepository) {
        this.jiraConfig = jiraConfig;
        this.restTemplate = restTemplate;
        this.jiraRepository = jiraRepository;
        this.projectRepository = projectRepository;
    }

    public boolean createProject(JiraDTO projectDto, Long projectId) {
        String apiUrl = jiraConfig.getApiUrl();
        String username = jiraConfig.getUsername();
        String password = jiraConfig.getPassword();

        String url = apiUrl + "/project";

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(username, password);
        headers.setContentType(MediaType.APPLICATION_JSON); // Set the content type to JSON

        HttpEntity<JiraDTO> requestEntity = new HttpEntity<>(projectDto, headers);


        try {
            ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, Void.class);

            if (response.getStatusCode() == HttpStatus.CREATED) {
                Jira jira = new Jira();
                jira.setName(projectDto.getName());
                jira.setJiraKey(projectDto.getKey());
                jira.setProjectTypeKey(projectDto.getProjectTypeKey());

                // Fetch the Project entity from the repository using projectId
                Optional<Project> projectOptional = projectRepository.findById(projectId);
                if (projectOptional.isPresent()) {
                    Project project = projectOptional.get();
                    jira.setProject(project);
                } else {
                    // Handle the case where the Project is not found
                    return false;
                }

                jiraRepository.save(jira);

                return true;
            } else {
                return false;
            }
        } catch (HttpClientErrorException ex) {
            // Handle error here if needed
            return false;
        }

    }

    public List<Map<String, String>> getJiraProjectsWithProjectNames() {
        List<Jira> jiraProjects = jiraRepository.findAll();
        List<Map<String, String>> result = new ArrayList<>();

        for (Jira jiraProject : jiraProjects) {
            //craete map to hold info
            Map<String, String> projectInfo = new HashMap<>();
            projectInfo.put("jiraProjectName", jiraProject.getName());
            //Check if the Jira project has project
            if (jiraProject.getProject() != null) {
                Project localProject = jiraProject.getProject();
                //adding project name
                projectInfo.put("projectName", localProject.getProjectName());
            }
            result.add(projectInfo);
        }
        return result;
    }

}