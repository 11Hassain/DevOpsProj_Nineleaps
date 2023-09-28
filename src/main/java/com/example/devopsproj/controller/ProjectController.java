package com.example.devopsproj.controller;

import com.example.devopsproj.commons.enumerations.EnumRole;
import com.example.devopsproj.dto.responsedto.*;
import com.example.devopsproj.exceptions.NotFoundException;
import com.example.devopsproj.model.*;
import com.example.devopsproj.repository.GitRepositoryRepository;
import com.example.devopsproj.repository.ProjectRepository;
import com.example.devopsproj.repository.UserRepository;
import com.example.devopsproj.service.interfaces.GitHubCollaboratorService;
import com.example.devopsproj.service.interfaces.IUserService;
import com.example.devopsproj.service.interfaces.JwtService;
import com.example.devopsproj.service.interfaces.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final IUserService userService;
    private final GitRepositoryRepository gitRepositoryRepository;
    private final JwtService jwtService;
    private final GitHubCollaboratorService collaboratorService;

    // Create a new project and return its details.
    @PostMapping("/create")
    public ResponseEntity<Object> createProject(@RequestBody ProjectDTO projectDTO) {
        // Create the project and return its details in the response.
        ProjectDTO createdProjectDTO = projectService.createProject(projectDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProjectDTO);
    }

    // Get project details by ID.
    @GetMapping("/{id}")
    public ResponseEntity<Object> getProjectById(@PathVariable("id") Long id) {
        ProjectDTO projectDTO = projectService.getProjectById(id);
        return new ResponseEntity<>(projectDTO, HttpStatus.OK);
    }


    // Get a list of all projects.
    @GetMapping("/all")
    public ResponseEntity<Object> getAll() {
        List<ProjectDTO> projectDTOs = projectService.getAll();
        return new ResponseEntity<>(projectDTOs, HttpStatus.OK);
    }


    // Get a list of all projects along with associated users.
    @GetMapping("/allProjects")
    public ResponseEntity<Object> getAllProjectsWithUsers() {
        List<ProjectWithUsersDTO> projectsWithUsers = projectService.getAllProjectsWithUsers();
        return new ResponseEntity<>(projectsWithUsers, HttpStatus.OK);
    }

    // Get a list of users in a specific project.
    @GetMapping("/{projectId}/users")
    public ResponseEntity<Object> getAllUsersByProjectId(@PathVariable Long projectId) {
        List<UserDTO> userDTOList = projectService.getAllUsersByProjectId(projectId);
        return ResponseEntity.ok(userDTOList);
    }
    // Get a list of all users in a project by their role.
    @GetMapping("/{projectId}/users/{role}")
    public ResponseEntity<Object> getAllUsersByProjectIdByRole(
            @PathVariable Long projectId,
            @PathVariable String role) {
        EnumRole enumRole = EnumRole.valueOf(role.toUpperCase());

        List<UserDTO> userDTOList = projectService.getAllUsersByProjectIdAndRole(projectId, enumRole);
        return ResponseEntity.ok(userDTOList);
    }

    // Update an existing project.
    @PutMapping("/update/{projectId}")
    public ResponseEntity<ProjectDTO> updateProject(@PathVariable("projectId") Long projectId,
                                                    @RequestBody ProjectDTO projectDTO) {
        ProjectDTO updatedProjectDTO = projectService.updateProject(projectId, projectDTO);
        return new ResponseEntity<>(updatedProjectDTO, HttpStatus.OK);
    }


    // Soft delete a project.
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteProject(@PathVariable("id") Long id) {
        ResponseEntity<String> response = projectService.deleteProject(id);
        return response;
    }
    // Add a user to a project.
    @PutMapping("/{projectId}/users/{userId}")
    public ResponseEntity<Object> addUserToProject(
            @PathVariable("projectId") Long projectId,
            @PathVariable("userId") Long userId) {
        return projectService.addUserToProject(projectId, userId);
    }
    // Remove a user from a project.

    @DeleteMapping("/{projectId}/users/{userId}")
    public ResponseEntity<String> removeUserFromProject(
            @PathVariable("projectId") Long projectId,
            @PathVariable("userId") Long userId) {
        return projectService.removeUserFromProject(projectId, userId);
    }

    @DeleteMapping("/{projectId}/users/{userId}/repo")
    public ResponseEntity<String> removeUserFromProjectAndRepo(
            @PathVariable("projectId") Long projectId,
            @PathVariable("userId") Long userId,
            @RequestBody CollaboratorDTO collaboratorDTO) {
        return projectService.removeUserFromProjectAndRepo(projectId, userId, collaboratorDTO);
    }

    // Get users by project ID and role.
    @GetMapping("/{projectId}/users/role/{role}")
    public ResponseEntity<List<UserDTO>> getUsersByProjectIdAndRole(
            @PathVariable("projectId") Long projectId,
            @PathVariable("role") String role) {
        List<UserDTO> userDTOList = projectService.getUsersByProjectIdAndRole(projectId, role);
        return new ResponseEntity<>(userDTOList, HttpStatus.OK);
    }

    // Add a repository to a project.

    @PutMapping("/{projectId}/repository/{repoId}")
    public ResponseEntity<Object> addRepositoryToProject(
            @PathVariable("projectId") Long projectId,
            @PathVariable("repoId") Long repoId) {
        return projectService.addRepositoryToProject(projectId, repoId);
    }

    // Get projects without a Figma URL.
    @GetMapping("/without-figma-url")
    public ResponseEntity<List<ProjectDTO>> getProjectsWithoutFigmaURL() {
        List<ProjectDTO> projectsWithoutFigmaURL = projectService.getProjectsWithoutFigmaURL();
        return ResponseEntity.ok(projectsWithoutFigmaURL);
    }

    @GetMapping("/without-google-drive")
    public ResponseEntity<List<ProjectDTO>> getProjectsWithoutGoogleDriveLink() {
        List<ProjectDTO> projectsWithoutGoogleDriveLink = projectService.getProjectsWithoutGoogleDriveLink();
        return ResponseEntity.ok(projectsWithoutGoogleDriveLink);
    }

    // Count all people by project ID and name.
    @GetMapping("/countPeople")
    public ResponseEntity<List<ProjectNamePeopleCountDTO>> countAllPeopleByProjectIdAndName() {
        List<ProjectNamePeopleCountDTO> peopleCountDTOs = projectService.getCountAllPeopleAndProjectName();
        return ResponseEntity.ok(peopleCountDTOs);
    }

    @GetMapping("/count")
    public ResponseEntity<Object> countAllProjects() {
        Integer countProjects = projectService.getCountAllProjects();
        return ResponseEntity.ok(countProjects);
    }


    @GetMapping("/count/role/{role}")
    public ResponseEntity<Object> countAllProjectsByRole(@PathVariable("role") String role) {
        EnumRole enumRole = EnumRole.valueOf(role.toUpperCase());
        Integer countProjects = projectService.getCountAllProjectsByRole(enumRole);
        return ResponseEntity.ok(countProjects);
    }


    @GetMapping("/count/user/{userId}")
    public ResponseEntity<Object> countAllProjectsByUserId(@PathVariable("userId") Long id) {
        Integer countProjects = projectService.getCountAllProjectsByUserId(id);
        return ResponseEntity.ok(countProjects);
    }


    @GetMapping("/{projectId}/count")
    public ResponseEntity<Object> countAllUsersByProjectId(@PathVariable Long projectId) {
        Integer countUsers = projectService.getCountAllUsersByProjectId(projectId);
        return ResponseEntity.ok(countUsers);
    }

    // Count all users by project ID and role.
    @GetMapping("/{projectId}/count/{role}")
    public ResponseEntity<Object> countAllUsersByProjectIdByRole(
            @PathVariable Long projectId,
            @PathVariable String role) {
        EnumRole enumRole = EnumRole.valueOf(role.toUpperCase());
        Integer countUsers = projectService.getCountAllUsersByProjectIdAndRole(projectId, enumRole);
        return ResponseEntity.ok(countUsers);
    }


    // Count all active projects.
    @GetMapping("/count/active")
    public ResponseEntity<Object> countAllActiveProjects() {
        Integer countProjects = projectService.getCountAllActiveProjects();
        return ResponseEntity.ok(countProjects);
    }


    // Count all inactive projects.
    @GetMapping("/count/inactive")
    public ResponseEntity<Object> countAllInActiveProjects() {
        Integer countProjects = projectService.getCountAllInActiveProjects();
        return ResponseEntity.ok(countProjects);
    }


    // Get project details by ID.
    @GetMapping("/{projectId}/details")
    public ResponseEntity<Object> getProjectDetailsById(@PathVariable Long projectId) {
        ProjectDTO projectDetails = projectService.getProjectDetailsById(projectId);
        return new ResponseEntity<>(projectDetails, HttpStatus.OK);
    }

}