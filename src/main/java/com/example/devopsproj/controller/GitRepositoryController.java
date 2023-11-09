package com.example.devopsproj.controller;
import com.example.devopsproj.commons.enumerations.EnumRole;
import com.example.devopsproj.constants.CommonConstants;
import com.example.devopsproj.dto.responsedto.GitRepositoryDTO;
import com.example.devopsproj.model.GitRepository;
import com.example.devopsproj.service.interfaces.GitRepositoryService;
import io.swagger.annotations.ApiOperation;
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

    /**
     * Create a new Git repository.
     *
     * @param gitRepository The GitRepository object containing repository details.
     * @return ResponseEntity indicating the status of the repository creation.
     */
    @PostMapping("/add")
    @ApiOperation("Create a Git repository")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createRepository(@RequestBody GitRepository gitRepository) {
        return ResponseEntity.ok(gitRepositoryService.createRepository(gitRepository));
    }

    /**
     * Get a list of all Git repositories.
     *
     * @return ResponseEntity containing a list of all Git repositories.
     */
    @GetMapping("/get")
    @ApiOperation("Get a list of all Git repositories")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getAllRepositories() {
        return ResponseEntity.ok(gitRepositoryService.getAllRepositories());
    }

    /**
     * Get a list of Git repositories by project ID.
     *
     * @param id The project ID.
     * @return ResponseEntity containing a list of Git repositories associated with the project.
     */
    @GetMapping("/project/{id}")
    @ApiOperation("Get a list of Git repositories by project ID")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getAllReposByProject(@PathVariable Long id) {
        List<GitRepositoryDTO> gitRepositoryDTOS = gitRepositoryService.getAllRepositoriesByProject(id);
        return new ResponseEntity<>(gitRepositoryDTOS, HttpStatus.OK);
    }


    /**
     * Get a list of Git repositories by role.
     *
     * @param role The role name.
     * @return ResponseEntity containing a list of Git repositories with the specified role.
     */
    @GetMapping("/get/role/{role}")
    @ApiOperation("Get a list of Git repositories by role")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getAllReposByRole(@PathVariable("role") String role) {
        EnumRole enumRole = EnumRole.valueOf(role.toUpperCase());
        return ResponseEntity.ok(gitRepositoryService.getAllReposByRole(enumRole));
    }


    /**
     * Get a Git repository by ID.
     *
     * @param id The ID of the Git repository to retrieve.
     * @return ResponseEntity containing the GitRepositoryDTO with repository details.
     */
    @GetMapping("/get/{id}")
    @ApiOperation("Get a Git repository by ID")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getRepositoryById(@PathVariable Long id) {
        GitRepository repository = gitRepositoryService.getRepositoryById(id);
        GitRepositoryDTO repositoryDTO = new GitRepositoryDTO(repository.getName(), repository.getDescription());
        return ResponseEntity.ok(repositoryDTO);
    }

    /**
     * Delete a Git repository by ID.
     *
     * @param repoId The ID of the Git repository to delete.
     * @return ResponseEntity indicating the status of the repository deletion.
     */
        @DeleteMapping("/delete/{repoId}")
        @ApiOperation("Delete a Git repository by ID")
        @ResponseStatus(HttpStatus.OK)
        public ResponseEntity<Object> deleteRepository(@PathVariable Long repoId) {
            gitRepositoryService.deleteRepository(repoId);
            return ResponseEntity.ok(CommonConstants.DELETED_SUCCESSFULLY);
        }

}