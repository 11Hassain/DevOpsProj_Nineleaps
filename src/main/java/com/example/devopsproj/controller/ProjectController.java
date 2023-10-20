package com.example.devopsproj.controller;

import com.example.devopsproj.commons.enumerations.EnumRole;
import com.example.devopsproj.dto.responsedto.*;
import com.example.devopsproj.exceptions.ConflictException;
import com.example.devopsproj.model.Project;
import com.example.devopsproj.model.User;
import com.example.devopsproj.model.UserNames;
import com.example.devopsproj.service.implementations.ProjectServiceImpl;
import com.example.devopsproj.exceptions.NotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * The ProjectController class provides RESTful API endpoints for managing projects. It allows creating new projects and retrieving project details.
 * It also provides other functionalities performed using the projects.
 * User authentication is required using the JwtServiceImpl.
 *
 * @version 2.0
 */

@RestController
@RequestMapping("/api/v1/projects")
@Validated
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectServiceImpl projectServiceImpl;
    private static final String INTERNAL_SERVER_ERROR = "Something went wrong";


    @PostMapping("/create")
    @Operation(
            description = "Create Project",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Project created successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createProject(@Valid @RequestBody ProjectDTO projectDTO) {

            ProjectDTO createdProjectDTO = projectServiceImpl.createProject(projectDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdProjectDTO);
    }

    @GetMapping("/{id}") // Get project by id
    @Operation(
            description = "Get Project by ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Project retrieved successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "Project not found"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getProjectById(@PathVariable("id") Long id) {

        try {
            return projectServiceImpl.getProject(id);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/all") // Retrieve a list of all projects
    @Operation(
            description = "Retrieve a list of all projects",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Projects retrieved successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "No projects found"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getAll() {

            try {
                List<Project> projects = projectServiceImpl.getAll();
                List<ProjectDTO> projectDTOs = projects.stream()
                        .map(project -> new ProjectDTO(project.getProjectId(), project.getProjectName(),
                                project.getProjectDescription(), project.getLastUpdated(), project.getDeleted()))
                        .toList();
                return new ResponseEntity<>(projectDTOs, HttpStatus.OK);

            } catch (NotFoundException e) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

    }

    @GetMapping("/allProjects")
    @Operation(
            description = "Retrieve a list of all projects with users",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Projects retrieved successfully with users"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "No projects found"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getAllProjectsWithUsers() {

        try {
            List<ProjectWithUsersDTO> projectsWithUsers = projectServiceImpl.getAllProjectsWithUsers();
            return new ResponseEntity<>(projectsWithUsers, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Get a list of users in the project
    @GetMapping("/{projectId}/users")
    @Operation(
            description = "Retrieve a list of users in the project",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "204", description = "No users found"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getAllUsersByProjectId(@PathVariable Long projectId) {
        try {
            List<UserDTO> userDTOList = projectServiceImpl.getAllUsersByProjectId(projectId);
            if (userDTOList.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return ResponseEntity.ok(userDTOList);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{projectId}/users/{role}")
    @Operation(
            description = "Retrieve a list of users in the project by role",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Users retrieved successfully by role"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "204", description = "No users found by role"),
                    @ApiResponse(responseCode = "404", description = "Role not found"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getAllUsersByProjectIdByRole(
            @PathVariable Long projectId,
            @PathVariable String role) {

            try {
                EnumRole enumRole = EnumRole.valueOf(role.toUpperCase());
                List<User> userList = projectServiceImpl.getAllUsersByProjectIdAndRole(projectId, enumRole);
                if (userList.isEmpty()) {
                    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
                }

                List<UserDTO> userDTOList = userList.stream()
                        .map(user -> {
                            UserNames usernames = user.getUserNames();
                            String username = (usernames != null) ? usernames.getUsername() : null;
                            return new UserDTO(user.getId(), user.getName(), user.getEmail(), user.getEnumRole(), username);
                        })
                        .toList();

                return ResponseEntity.ok(userDTOList);
            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }

    }

    @PutMapping("/update/{projectId}") // Update project
    @Operation(
            description = "Update a project",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Project updated successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "Project not found"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> updateProject(@PathVariable("projectId") Long projectId,
                                                @Valid @RequestBody ProjectDTO projectDTO) {

            try {
                Optional<Project> optionalProject = projectServiceImpl.getProjectById(projectId);
                if (optionalProject.isPresent()) {
                    Project existingProject = optionalProject.get();
                    existingProject.setProjectName(projectDTO.getProjectName());
                    existingProject.setProjectDescription(projectDTO.getProjectDescription());
                    existingProject.setLastUpdated(LocalDateTime.now());
                    Project updatedProject = projectServiceImpl.updateProject(existingProject);
                    ProjectDTO updatedProjectDTO = new ProjectDTO(updatedProject.getProjectId(), updatedProject.getProjectName(), updatedProject.getProjectDescription(), updatedProject.getLastUpdated());
                    return new ResponseEntity<>(updatedProjectDTO, HttpStatus.OK);
                } else {
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }
            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }

    }

    @DeleteMapping("/delete/{id}") // Delete project (soft)
    @Operation(
            description = "Delete a project (soft)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Project deleted successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "Project not found"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> deleteProject(@PathVariable("id") Long id) {

            if (projectServiceImpl.existsProjectById(id)) {
                boolean checkIfDeleted = projectServiceImpl.existsByIdIsDeleted(id);
                if (checkIfDeleted) {
                    return ResponseEntity.ok("Project doesn't exist");
                }
                boolean isDeleted = projectServiceImpl.softDeleteProject(id);
                if (isDeleted) {
                    return ResponseEntity.ok("Deleted project successfully");
                } else {
                    return ResponseEntity.notFound().build();
                }
            } else return ResponseEntity.notFound().build();

    }

    @PutMapping("/{projectId}/users/{userId}") // Add user to project
    @Operation(
            description = "Add a user to the project",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User added to project successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "Project or User not found"),
                    @ApiResponse(responseCode = "409", description = "Conflict - User already in project"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> addUserToProject(
            @PathVariable("projectId") Long projectId,
            @PathVariable("userId") Long userId) {

            try {
                ResponseEntity<Object> response = projectServiceImpl.addUserToProjectByUserIdAndProjectId(projectId, userId);
                return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
            } catch (NotFoundException e){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Resource not found");
            } catch (ConflictException e) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("User already exists in the project");
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(INTERNAL_SERVER_ERROR);
            }

    }

    @DeleteMapping("/{projectId}/users/{userId}") // Remove user from the project
    @Operation(
            description = "Remove a user from the project",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User removed from project successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "Project or User not found"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> removeUserFromProject(
            @PathVariable("projectId") Long projectId,
            @PathVariable("userId") Long userId) {

            try {
                ResponseEntity<String> response = projectServiceImpl.removeUserFromProjectByUserIdAndProjectId(projectId, userId);
                return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
            } catch (NotFoundException e){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Resource not found");
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(INTERNAL_SERVER_ERROR);
            }

    }

    @DeleteMapping("/{projectId}/users/{userId}/repo") // Remove user from the project and repo as well
    @Operation(
            description = "Remove a user from the project and repo as well",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User removed from project and repo successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "Project or User not found"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> removeUserFromProjectAndRepo(
            @PathVariable("projectId") Long projectId,
            @PathVariable("userId") Long userId,
            @Valid @RequestBody CollaboratorDTO collaboratorDTO) {

            try {
                ResponseEntity<String> response = projectServiceImpl.removeUserFromProjectAndRepo(projectId, userId, collaboratorDTO);
                if (response.getStatusCode() == HttpStatus.BAD_REQUEST){
                    return ResponseEntity.badRequest().body("Unable to remove user");
                } else {
                    return ResponseEntity.ok("User removed successfully");
                }
            } catch (NotFoundException e){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Project or User not found");
            } catch (Exception e){
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(INTERNAL_SERVER_ERROR);
            }

    }

    @GetMapping("/{projectId}/users/role/{role}") // Get users based on role
    @Operation(
            description = "Get users by role in the project",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "Project not found"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getUsersByProjectIdAndRole(
            @PathVariable("projectId") Long projectId,
            @PathVariable("role") String role) {

            List<UserDTO> userDTOList = projectServiceImpl.getUsersByProjectIdAndRole(projectId, role);
            return new ResponseEntity<>(userDTOList, HttpStatus.OK);

    }

    @PutMapping("/{projectId}/repository/{repoId}") // Add Repo to Project based on Project ID, Repo ID
    @Operation(
            description = "Add a repository to the project",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Repository added to project successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "Project or Repository not found"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> addRepositoryToProject(
            @PathVariable("projectId") Long projectId,
            @PathVariable("repoId") Long repoId) {

            try {
                ResponseEntity<Object> result = projectServiceImpl.addRepositoryToProject(projectId, repoId);
                if (result.getStatusCode() == HttpStatus.NOT_FOUND){
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Repository or Project not found");
                }else {
                    return ResponseEntity.ok("Stored successfully");
                }
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }

    }

    @GetMapping("/without-figma-url")
    @Operation(
            description = "Get projects without Figma URL",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Projects retrieved successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getProjectsWithoutFigmaURL() {

            List<ProjectDTO> projects = projectServiceImpl.getProjectsWithoutFigmaURL();
            return ResponseEntity.ok(projects);

    }

    @GetMapping("/without-google-drive")
    @Operation(
            description = "Get projects without Google Drive Link",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Projects retrieved successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getProjectsWithoutGoogleDriveLink() {

            List<ProjectDTO> projects = projectServiceImpl.getProjectsWithoutGoogleDriveLink();
            return ResponseEntity.ok(projects);

    }

    @GetMapping("/countPeople")
    @Operation(
            description = "Count people by project name",
            responses = {
                    @ApiResponse(responseCode = "200", description = "People count retrieved successfully"),
                    @ApiResponse(responseCode = "204", description = "No Content - Empty result"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    public ResponseEntity<Object> countAllPeopleByProjectIdAndName() {

            List<ProjectNamePeopleCountDTO> peopleCountDTOs = projectServiceImpl.getCountAllPeopleAndProjectName();
            if (peopleCountDTOs.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Empty");
            } else {
                return ResponseEntity.ok(peopleCountDTOs);
            }

    }

    @GetMapping("/count")
    @Operation(
            description = "Count all projects",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Projects count retrieved successfully"),
                    @ApiResponse(responseCode = "204", description = "No Content - Empty result"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    public ResponseEntity<Object> countAllProjects() {

            Integer countProjects = projectServiceImpl.getCountAllProjects();
            if (countProjects == 0) {
                return ResponseEntity.ok(0);
            } else {
                return ResponseEntity.ok(countProjects);
            }

    }

    @GetMapping("/count/role/{role}")
    @Operation(
            description = "Count all projects by role",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Projects count retrieved successfully"),
                    @ApiResponse(responseCode = "204", description = "No Content - Empty result"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    public ResponseEntity<Object> countAllProjectsByRole(@PathVariable("role") String role) {

            EnumRole enumRole = EnumRole.valueOf(role.toUpperCase());
            Integer countProjects = projectServiceImpl.getCountAllProjectsByRole(enumRole);
            if (countProjects == 0) {
                return ResponseEntity.ok(0);
            } else {
                return ResponseEntity.ok(countProjects);
            }

    }

    @GetMapping("/count/user/{userId}")
    @Operation(
            description = "Count all projects by user ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Projects count retrieved successfully"),
                    @ApiResponse(responseCode = "204", description = "No Content - Empty result"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    public ResponseEntity<Object> countAllProjectsByUserId(@PathVariable("userId") Long id) {

            Integer countProjects = projectServiceImpl.getCountAllProjectsByUserId(id);
            if (countProjects == 0) {
                return ResponseEntity.ok(0);
            } else {
                return ResponseEntity.ok(countProjects);
            }

    }

    @GetMapping("/{projectId}/count")
    @Operation(
            description = "Count all users in a project by project ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Users count retrieved successfully"),
                    @ApiResponse(responseCode = "204", description = "No Content - Empty result"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    public ResponseEntity<Object> countAllUsersByProjectId(@PathVariable Long projectId) {

            Integer countUsers = projectServiceImpl.getCountAllUsersByProjectId(projectId);
            if (countUsers == 0) {
                return ResponseEntity.ok(0);
            } else {
                return ResponseEntity.ok(countUsers);
            }

    }

    @GetMapping("/{projectId}/count/{role}")
    @Operation(
            description = "Count all users in a project by role and project ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Users count retrieved successfully"),
                    @ApiResponse(responseCode = "204", description = "No Content - Empty result"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    public ResponseEntity<Object> countAllUsersByProjectIdByRole(
            @PathVariable Long projectId,
            @PathVariable String role) {

            EnumRole enumRole = EnumRole.valueOf(role.toUpperCase());
            Integer countUsers = projectServiceImpl.getCountAllUsersByProjectIdAndRole(projectId, enumRole);
            if (countUsers == 0) {
                return ResponseEntity.ok(0);
            } else {
                return ResponseEntity.ok(countUsers);
            }

    }

    @GetMapping("/count/active")
    @Operation(
            description = "Count all active projects",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Projects count retrieved successfully"),
                    @ApiResponse(responseCode = "204", description = "No Content - Empty result"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    public ResponseEntity<Object> countAllActiveProjects() {

            Integer countProjects = projectServiceImpl.getCountAllActiveProjects();
            if (countProjects == 0) {
                return ResponseEntity.ok(0);
            } else {
                return ResponseEntity.ok(countProjects);
            }

    }

    @GetMapping("/count/inactive")
    @Operation(
            description = "Count all inactive projects",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Projects count retrieved successfully"),
                    @ApiResponse(responseCode = "204", description = "No Content - Empty result"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    public ResponseEntity<Object> countAllInActiveProjects() {

            Integer countProjects = projectServiceImpl.getCountAllInActiveProjects();
            if (countProjects == 0) {
                return ResponseEntity.ok(0);
            } else {
                return ResponseEntity.ok(countProjects);
            }

    }

    @GetMapping("/{projectId}/details")
    @Operation(
            description = "Get project details by project ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Project details retrieved successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "500", description = "Internal Server Error")
            }
    )
    public ResponseEntity<Object> getProjectDetailsById(@PathVariable Long projectId) {

            try {
                ProjectDTO projectDetails = projectServiceImpl.getProjectDetailsById(projectId);
                return new ResponseEntity<>(projectDetails, HttpStatus.OK);
            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }

    }
}
