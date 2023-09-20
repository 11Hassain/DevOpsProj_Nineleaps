package com.example.devopsproj.controller;
import com.example.devopsproj.commons.enumerations.EnumRole;
import com.example.devopsproj.dto.responseDto.GitRepositoryDTO;
import com.example.devopsproj.model.GitRepository;
import com.example.devopsproj.service.interfaces.GitRepositoryService;
import com.example.devopsproj.service.interfaces.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



import java.util.List;

@RestController
@RequestMapping("/api/v1/repositories")
@RequiredArgsConstructor
public class GitRepositoryController {
    private final GitRepositoryService gitRepositoryService;
    private final JwtService jwtService;

    private static final String INVALID_TOKEN = "Invalid Token";

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createRepository(@RequestBody GitRepository gitRepository,
                                                   @RequestHeader("AccessToken") String accessToken) {
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            return ResponseEntity.ok(gitRepositoryService.createRepository(gitRepository));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
        }
    }

    @GetMapping("/get")
    public ResponseEntity<Object> getAllRepositories(@RequestHeader("AccessToken") String accessToken) {
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            return ResponseEntity.ok(gitRepositoryService.getAllRepositories());
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
        }
    }

    @GetMapping("/project/{id}")
    public ResponseEntity<Object> getAllReposByProject(
            @PathVariable Long id,
            @RequestHeader("AccessToken") String accessToken){
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            List<GitRepositoryDTO> gitRepositoryDTOS = gitRepositoryService.getAllRepositoriesByProject(id);
            return new ResponseEntity<>(gitRepositoryDTOS, HttpStatus.OK);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
        }
    }

    @GetMapping("/get/role/{role}")
    public ResponseEntity<Object> getAllReposByRole(
            @PathVariable("role") String role,
            @RequestHeader("AccessToken") String accessToken) {
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            EnumRole enumRole = EnumRole.valueOf(role.toUpperCase());
            return ResponseEntity.ok(gitRepositoryService.getAllReposByRole(enumRole));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
        }
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<Object> getRepositoryById(
            @PathVariable Long id,
            @RequestHeader("AccessToken") String accessToken) {
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            GitRepository repository = gitRepositoryService.getRepositoryById(id);
            if (repository != null) {
                GitRepositoryDTO repositoryDTO = new GitRepositoryDTO(repository.getName(), repository.getDescription());
                return ResponseEntity.ok(repositoryDTO);
            } else {
                return ResponseEntity.notFound().build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
        }
    }

    @DeleteMapping("/delete/{repoId}")
    public ResponseEntity<Object> deleteRepository(
            @PathVariable Long repoId,
            @RequestHeader(value = "AccessToken", required = false) String accessToken
    ) {
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if(isTokenValid) {
            try {
                gitRepositoryService.deleteRepository(repoId); // Delete the repository
                return ResponseEntity.ok("Deleted successfully");

            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error");
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
        }
    }
}