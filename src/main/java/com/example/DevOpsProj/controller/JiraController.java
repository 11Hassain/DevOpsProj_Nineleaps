package com.example.DevOpsProj.controller;
import com.example.DevOpsProj.dto.responseDto.JiraProjectDTO;
import com.example.DevOpsProj.service.JiraService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
@RestController
@RequestMapping("/jira")
public class JiraController {
    private final JiraService jiraService;

    public JiraController(JiraService jiraService) {
        this.jiraService = jiraService;
    }

    @PostMapping("/create-project")
    @ResponseStatus(HttpStatus.CREATED)
    public void createProject(@RequestBody JiraProjectDTO projectDto) {
        jiraService.createProject(projectDto);
    }
}
