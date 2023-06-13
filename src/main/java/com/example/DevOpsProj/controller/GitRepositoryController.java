package com.example.DevOpsProj.controller;
import com.example.DevOpsProj.dto.responseDto.GitRepositoryDTO;
import com.example.DevOpsProj.model.GitRepository;
import com.example.DevOpsProj.service.GitRepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/repositories")
public class GitRepositoryController {

    private final GitRepositoryService gitRepositoryService;

    @Autowired
    public GitRepositoryController(GitRepositoryService gitRepositoryService) {
        this.gitRepositoryService = gitRepositoryService;
    }

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public GitRepository createRepository(@RequestBody GitRepository gitRepository) {
        return gitRepositoryService.createRepository(gitRepository);
    }
    @GetMapping("/get")
    public List<GitRepository> getAllRepositories() {
        return gitRepositoryService.getAllRepositories();
    }

    @GetMapping("/project/{id}")
    public ResponseEntity<List<GitRepositoryDTO>> getAllReposByProject(@PathVariable Long id){
        List<GitRepositoryDTO> gitRepositoryDTOS = gitRepositoryService.getAllRepositoriesByProject(id);
        return new ResponseEntity<>(gitRepositoryDTOS, HttpStatus.OK);
    }

}
