package com.example.devopsproj.controller;
import com.example.devopsproj.constants.GitHubCollaboratorConstants;
import com.example.devopsproj.dto.responsedto.CollaboratorDTO;
import com.example.devopsproj.service.interfaces.GitHubCollaboratorService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/collaborators")
@RequiredArgsConstructor
public class GitHubCollaboratorController {
    private final GitHubCollaboratorService collaboratorService;

    /**
     * Add a collaborator to a GitHub repository.
     *
     * @param collaboratorDTO The CollaboratorDTO containing collaborator details.
     * @return ResponseEntity indicating the status of the collaborator addition.
     */
    @PostMapping("/add")
    @ApiOperation("Add a collaborator")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<String> addCollaborator(@RequestBody CollaboratorDTO collaboratorDTO) {
        collaboratorService.addCollaborator(collaboratorDTO);
        return ResponseEntity.ok(GitHubCollaboratorConstants.ADD_COLLABORATOR_SUCCESS);
    }

    /**
     * Delete a collaborator from a GitHub repository.
     *
     * @param collaboratorDTO The CollaboratorDTO containing collaborator details to be deleted.
     * @return ResponseEntity indicating the status of the collaborator deletion.
     */
    @DeleteMapping("/delete")
    @ApiOperation("Delete a collaborator from a GitHub repository")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> deleteCollaborator(@RequestBody CollaboratorDTO collaboratorDTO) {
        collaboratorService.deleteCollaborator(collaboratorDTO);
        return ResponseEntity.ok(GitHubCollaboratorConstants.DELETE_COLLABORATOR_SUCCESS);
    }

}
