package com.example.DevOpsProj.controller;
import com.example.DevOpsProj.dto.responseDto.RepositoryDTO;
import com.example.DevOpsProj.model.Repository;
import com.example.DevOpsProj.repository.RepositoryRepository;
import com.example.DevOpsProj.service.RepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/repositories")
public class RepositoryController {

    private final RepositoryService repositoryService;

    @Autowired
    public RepositoryController(RepositoryService repositoryService) {
        this.repositoryService = repositoryService;
    }

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public Repository createRepository(@RequestBody Repository repository) {
        return repositoryService.createRepository(repository);
    }
    @GetMapping("/get")
    public List<Repository> getAllRepositories() {
        return repositoryService.getAllRepositories();
    }

}
