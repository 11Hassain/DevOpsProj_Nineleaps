package com.example.devopsproj.controller;

import com.example.devopsproj.commons.enumerations.EnumRole;
import com.example.devopsproj.model.GitRepository;
import com.example.devopsproj.service.GitRepositoryService;
import com.example.devopsproj.service.JwtService;
import com.example.devopsproj.dto.responseDto.GitRepositoryDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/repositories")
@Validated
public class GitRepositoryController {

    @Autowired
    private GitRepositoryService gitRepositoryService;
    @Autowired
    private JwtService jwtService;

    private static final String INVALID_TOKEN = "Invalid Token";

    @PostMapping("/add")
    @Operation(
            description = "Create Git Repository",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Git Repository created successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
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
    @Operation(
            description = "Get All Git Repositories",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Git Repositories retrieved successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getAllRepositories(@RequestHeader("AccessToken") String accessToken) {
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            return ResponseEntity.ok(gitRepositoryService.getAllRepositories());
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
        }
    }

    @GetMapping("/project/{id}")
    @Operation(
            description = "Get All Git Repositories by Project",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Git Repositories by Project retrieved successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    @ResponseStatus(HttpStatus.OK)
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
    @Operation(
            description = "Get All Git Repositories by Role",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Git Repositories by Role retrieved successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    @ResponseStatus(HttpStatus.OK)
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
    @Operation(
            description = "Get Git Repository by ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Git Repository retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "Git Repository not found"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    @ResponseStatus(HttpStatus.OK)
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
    @Operation(
            description = "Delete Git Repository by ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Git Repository deleted successfully"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    @ResponseStatus(HttpStatus.OK)
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
