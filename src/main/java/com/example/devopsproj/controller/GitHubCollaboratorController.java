package com.example.devopsproj.controller;
import com.example.devopsproj.dto.responsedto.CollaboratorDTO;
import com.example.devopsproj.service.interfaces.GitHubCollaboratorService;
import com.example.devopsproj.service.interfaces.JwtService;
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
    private final JwtService jwtService;
    private static final String INVALID_TOKEN = "Invalid Token";

    // Add a collaborator to a GitHub repository.
    @PostMapping("/add")
    @ApiOperation("Add a collaborator")
    @ResponseStatus(HttpStatus.CREATED) // Replace with the appropriate status code
    public ResponseEntity<String> addCollaborator(@RequestBody CollaboratorDTO collaboratorDTO) {
        collaboratorService.addCollaborator(collaboratorDTO);
        return ResponseEntity.ok("Invitation to add collaborator sent successfully.");
    }


    // Delete a collaborator from a GitHub repository.
    @DeleteMapping("/delete")
    @ApiOperation("Delete a collaborator from a GitHub repository")
    @ResponseStatus(HttpStatus.OK) // Replace with the appropriate status code
    public ResponseEntity<String> deleteCollaborator(@RequestBody CollaboratorDTO collaboratorDTO) {
        collaboratorService.deleteCollaborator(collaboratorDTO);
        return ResponseEntity.ok("Collaborator removed successfully.");
    }

}
