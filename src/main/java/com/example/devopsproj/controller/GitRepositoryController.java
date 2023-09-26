package com.example.devopsproj.controller;
import com.example.devopsproj.commons.enumerations.EnumRole;
import com.example.devopsproj.dto.responsedto.GitRepositoryDTO;
import com.example.devopsproj.model.GitRepository;
import com.example.devopsproj.service.interfaces.GitRepositoryService;
import com.example.devopsproj.service.interfaces.JwtService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



import java.util.List;

@RestController
@RequestMapping("/api/v1/repositories")
@RequiredArgsConstructor
public class GitRepositoryController {
    private final GitRepositoryService gitRepositoryService;

    // Create a new Git repository.
    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createRepository(@RequestBody GitRepository gitRepository) {
        return ResponseEntity.ok(gitRepositoryService.createRepository(gitRepository));
    }

    // Get a list of all Git repositories.
    @GetMapping("/get")
    public ResponseEntity<Object> getAllRepositories() {
        return ResponseEntity.ok(gitRepositoryService.getAllRepositories());
    }

    // Get a list of Git repositories by project ID.
    @GetMapping("/project/{id}")
    public ResponseEntity<Object> getAllReposByProject(@PathVariable Long id) {
        List<GitRepositoryDTO> gitRepositoryDTOS = gitRepositoryService.getAllRepositoriesByProject(id);
        return new ResponseEntity<>(gitRepositoryDTOS, HttpStatus.OK);
    }

    // Get a list of Git repositories by role.
    @GetMapping("/get/role/{role}")
    public ResponseEntity<Object> getAllReposByRole(@PathVariable("role") String role) {
        EnumRole enumRole = EnumRole.valueOf(role.toUpperCase());
        return ResponseEntity.ok(gitRepositoryService.getAllReposByRole(enumRole));
    }

    // Get a Git repository by ID.
    @GetMapping("/get/{id}")
    public ResponseEntity<Object> getRepositoryById(@PathVariable Long id) {
        GitRepository repository = gitRepositoryService.getRepositoryById(id);
        GitRepositoryDTO repositoryDTO = new GitRepositoryDTO(repository.getName(), repository.getDescription());
        return ResponseEntity.ok(repositoryDTO);
    }

    // Delete a Git repository by ID.
    @DeleteMapping("/delete/{repoId}")
    public ResponseEntity<Object> deleteRepository(@PathVariable Long repoId) {
        gitRepositoryService.deleteRepository(repoId);
        return ResponseEntity.ok("Deleted successfully");
    }
}