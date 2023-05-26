package com.example.DevOpsProj.controller;
import com.example.DevOpsProj.dto.responseDto.CollaboratorDTO;
import com.example.DevOpsProj.service.GitHubCollaboratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/collaborators")
public class GitHubCollaboratorController {
    private final GitHubCollaboratorService collaboratorService;

    @Autowired
    public GitHubCollaboratorController(GitHubCollaboratorService collaboratorService) {
        this.collaboratorService = collaboratorService;
    }

    @PostMapping("/add")
    public ResponseEntity<String> addCollaborator(@RequestBody CollaboratorDTO collaboratorDTO) {
        boolean added = collaboratorService.addCollaborator(collaboratorDTO);
        if (added) {
            return ResponseEntity.ok("Invitation to add collaborator sent successfully.");
        } else {
            return ResponseEntity.badRequest().body("Failed to add collaborator.");
        }
    }
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteCollaborator(@RequestBody CollaboratorDTO collaboratorDTO) {
        boolean deleted = collaboratorService.deleteCollaborator(collaboratorDTO);
        if (deleted) {
            return ResponseEntity.ok("Collaborator removed successfully.");
        } else {
            return ResponseEntity.badRequest().body("Failed to remove collaborator.");
        }
    }
}
