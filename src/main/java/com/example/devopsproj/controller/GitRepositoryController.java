package com.example.devopsproj.controller;

import com.example.devopsproj.commons.enumerations.EnumRole;
import com.example.devopsproj.model.GitRepository;
import com.example.devopsproj.dto.responsedto.GitRepositoryDTO;
import com.example.devopsproj.service.interfaces.GitRepositoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * The GitRepositoryController class is responsible for managing RESTful API endpoints related to Git repositories and their actions.
 * It provides endpoints for creating, retrieving, and deleting Git repositories, and includes role-based and project-based repository retrieval.
 * Authentication is performed using the JwtServiceImpl.
 *
 * @version 2.0
 */

@RestController
@RequestMapping("/api/v1/repositories")
@Validated
@RequiredArgsConstructor
public class GitRepositoryController {

    private final GitRepositoryService gitRepositoryService;
    private static final Logger logger = LoggerFactory.getLogger(GitRepositoryController.class);

    /**
     * Create a Git repository.
     *
     * @param gitRepository The GitRepository containing the repository data.
     * @return ResponseEntity with the created Git repository or appropriate error responses.
     */
    @PostMapping("/add")
    @Operation(
            description = "Create Git Repository",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Git Repository created successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createRepository(@Valid @RequestBody GitRepository gitRepository) {
        logger.info("Received a request to create a Git repository.");

        Object createdRepository = gitRepositoryService.createRepository(gitRepository);

        if (createdRepository != null) {
            logger.info("Git Repository created successfully.");
            return ResponseEntity.status(HttpStatus.CREATED).body(createdRepository);
        } else {
            logger.error("Failed to create Git Repository.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get all Git repositories.
     *
     * @return ResponseEntity with the retrieved Git repositories or appropriate error responses.
     */
    @GetMapping("/get")
    @Operation(
            description = "Get All Git Repositories",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Git Repositories retrieved successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getAllRepositories() {
        logger.info("Received a request to retrieve all Git repositories.");

        Object gitRepositories = gitRepositoryService.getAllRepositories();

        if (gitRepositories != null) {
            logger.info("Git Repositories retrieved successfully.");
            return ResponseEntity.ok(gitRepositories);
        } else {
            logger.error("Failed to retrieve Git Repositories.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get all Git repositories associated with a specific project.
     *
     * @param id The ID of the project for which to retrieve Git repositories.
     * @return ResponseEntity with the retrieved Git repositories or appropriate error responses.
     */
    @GetMapping("/project/{id}")
    @Operation(
            description = "Get All Git Repositories by Project",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Git Repositories by Project retrieved successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getAllReposByProject(@PathVariable Long id) {
        logger.info("Received a request to retrieve Git repositories for Project ID: {}", id);

        List<GitRepositoryDTO> gitRepositoryDTOS = gitRepositoryService.getAllRepositoriesByProject(id);
        logger.info("Git Repositories by Project retrieved successfully for Project ID: {}", id);

        return new ResponseEntity<>(gitRepositoryDTOS, HttpStatus.OK);
    }

    /**
     * Get all Git repositories associated with a specific role.
     *
     * @param role The role for which to retrieve Git repositories.
     * @return ResponseEntity with the retrieved Git repositories or appropriate error responses.
     */
    @GetMapping("/get/role/{role}")
    @Operation(
            description = "Get All Git Repositories by Role",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Git Repositories by Role retrieved successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getAllReposByRole(@PathVariable("role") String role) {
        logger.info("Received a request to retrieve Git repositories by Role: {}", role);

        EnumRole enumRole = EnumRole.valueOf(role.toUpperCase());
        Object gitRepositories = gitRepositoryService.getAllReposByRole(enumRole);

        logger.info("Git Repositories by Role retrieved successfully for Role: {}", role);
        return ResponseEntity.ok(gitRepositories);
    }

    /**
     * Get a Git repository by its ID.
     *
     * @param id The ID of the Git repository to retrieve.
     * @return ResponseEntity with the retrieved Git repository or an error response if not found.
     */
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
    public ResponseEntity<Object> getRepositoryById(@PathVariable Long id) {
        logger.info("Received a request to retrieve a Git repository by ID: {}", id);

        GitRepository repository = gitRepositoryService.getRepositoryById(id);
        if (repository != null) {
            logger.info("Git Repository retrieved successfully for ID: {}", id);
            GitRepositoryDTO repositoryDTO = new GitRepositoryDTO(repository.getName(), repository.getDescription());
            return ResponseEntity.ok(repositoryDTO);
        } else {
            logger.info("Git Repository not found for ID: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Delete a Git repository by its ID.
     *
     * @param repoId The ID of the Git repository to delete.
     * @return ResponseEntity indicating the result of the operation.
     */
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
    public ResponseEntity<Object> deleteRepository(@PathVariable Long repoId) {
        logger.info("Received a request to delete a Git repository by ID: {}", repoId);

        try {
            gitRepositoryService.deleteRepository(repoId);
            logger.info("Git Repository with ID {} deleted successfully.", repoId);
            return ResponseEntity.ok("Deleted successfully");
        } catch (Exception e) {
            logger.error("Failed to delete Git Repository with ID: {}", repoId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error");
        }
    }
}
