package com.example.DevOpsProj.controller;
import com.example.DevOpsProj.commons.enumerations.EnumRole;
import com.example.DevOpsProj.dto.responseDto.GitRepositoryDTO;
import com.example.DevOpsProj.exceptions.NotFoundException;
import com.example.DevOpsProj.model.GitRepository;
import com.example.DevOpsProj.service.GitRepositoryService;
import com.example.DevOpsProj.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/repositories")
public class GitRepositoryController {

    @Autowired
    private final GitRepositoryService gitRepositoryService;
    @Autowired
    private JwtService jwtService;

    @Autowired
    public GitRepositoryController(GitRepositoryService gitRepositoryService) {
        this.gitRepositoryService = gitRepositoryService;
    }

    @Value("${github.accessToken}")
    private String accessToken;

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createRepository(@RequestBody GitRepository gitRepository,
                                          @RequestHeader("AccessToken") String accessToken) {
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            return ResponseEntity.ok(gitRepositoryService.createRepository(gitRepository));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Token");
        }
    }

    @GetMapping("/get")
    public ResponseEntity<Object> getAllRepositories(@RequestHeader("AccessToken") String accessToken) {
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            return ResponseEntity.ok(gitRepositoryService.getAllRepositories());
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Token");
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
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Token");
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
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Token");
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
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Token");
        }
    }


    @DeleteMapping("/delete/{repoId}")
    public ResponseEntity<Object> deleteRepository(
            @PathVariable Long repoId,
            @RequestHeader(value = "AccessToken", required = false) String accessToken
    ) {
        boolean isTokenValid = false;
        if (accessToken != null && !accessToken.isEmpty()) {
            isTokenValid = jwtService.isTokenTrue(accessToken);
        }

//        try {
            gitRepositoryService.deleteRepository(repoId); // Delete the repository
            return ResponseEntity.ok("Deleted successfully");
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting repository");
//        }
    }
}
