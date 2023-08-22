package com.example.DevOpsProj.controller;

import com.example.DevOpsProj.dto.responseDto.JiraDTO;
import com.example.DevOpsProj.model.Jira;
import com.example.DevOpsProj.model.Project;
import com.example.DevOpsProj.service.JiraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/jira")
public class JiraController {

    @Autowired
    private final JiraService jiraService;

    public JiraController(JiraService jiraService) {
        this.jiraService = jiraService;
    }

    @PostMapping("/create-project")
    public ResponseEntity<String> createProject(@RequestBody JiraDTO projectDto, @RequestParam Long projectId) {
        boolean success = jiraService.createProject(projectDto, projectId);
        if (success) {
            return ResponseEntity.status(HttpStatus.CREATED).body("Project successfully created");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create project");
        }
    }
    @GetMapping("/jira-projects-with-local-projects")
    public ResponseEntity<List<Map<String, String>>> getJiraProjectsWithLocalProjectNames() {
        List<Map<String, String>> projects = jiraService.getJiraProjectsWithProjectNames();
        return ResponseEntity.ok(projects);
    }
}