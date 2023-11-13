package com.example.devopsproj.controller;

import com.example.devopsproj.commons.enumerations.EnumRole;
import com.example.devopsproj.dto.responsedto.*;
import com.example.devopsproj.exceptions.ConflictException;
import com.example.devopsproj.model.Project;
import com.example.devopsproj.model.User;
import com.example.devopsproj.model.UserNames;
import com.example.devopsproj.exceptions.NotFoundException;
import com.example.devopsproj.service.interfaces.ProjectService;
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

    private final ProjectService projectService;
    private static final String INTERNAL_SERVER_ERROR = "Something went wrong";
    private static final Logger logger = LoggerFactory.getLogger(ProjectController.class);

    /**
     * Create a new project.
     *
     * @param projectDTO The project data to create a new project.
     * @return ResponseEntity with the created project data or an error response.
     */
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
        logger.info("Received a request to create a new project");

        ProjectDTO createdProjectDTO = projectService.createProject(projectDTO);
        logger.info("Project created successfully: {}", createdProjectDTO.getProjectName());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProjectDTO);
    }

    /**
     * Get a project by its ID.
     *
     * @param id The ID of the project to retrieve.
     * @return ResponseEntity with the project data or an error response.
     */
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
        logger.info("Received a request to get a project with ID: {}", id);

        try {
            ResponseEntity<Object> responseEntity = projectService.getProject(id);
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                logger.info("Project retrieved successfully with ID: {}", id);
            } else {
                logger.error("Failed to retrieve the project with ID: {}", id);
            }
            return responseEntity;
        } catch (NotFoundException e) {
            logger.info("Project not found with ID: {}", id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Retrieve a list of all projects.
     *
     * @return ResponseEntity with a list of projects or an error response.
     */
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
        logger.info("Received a request to retrieve a list of all projects.");

        try {
            List<Project> projects = projectService.getAll();
            List<ProjectDTO> projectDTOs = projects.stream()
                    .map(project -> new ProjectDTO(project.getProjectId(), project.getProjectName(),
                            project.getProjectDescription(), project.getLastUpdated(), project.getDeleted()))
                    .toList();

            logger.info("Projects retrieved successfully.");
            return new ResponseEntity<>(projectDTOs, HttpStatus.OK);
        } catch (NotFoundException e) {
            logger.info("No projects found.");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Retrieve a list of all projects with users.
     *
     * @return ResponseEntity with a list of projects with users or an error response.
     */
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
        logger.info("Received a request to retrieve a list of all projects with users.");

        try {
            List<ProjectWithUsersDTO> projectsWithUsers = projectService.getAllProjectsWithUsers();

            logger.info("Projects with users retrieved successfully.");
            return new ResponseEntity<>(projectsWithUsers, HttpStatus.OK);
        } catch (NotFoundException e) {
            logger.info("No projects found.");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Retrieve a list of users in the project by project ID.
     *
     * @param projectId The ID of the project to retrieve users from.
     * @return ResponseEntity with a list of users or an error response.
     */
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
        logger.info("Received a request to retrieve a list of users in the project with ID: {}", projectId);

        try {
            List<UserDTO> userDTOList = projectService.getAllUsersByProjectId(projectId);
            if (userDTOList.isEmpty()) {
                logger.info("No users found for the project with ID: {}", projectId);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            logger.info("Users retrieved successfully for the project with ID: {}", projectId);
            return ResponseEntity.ok(userDTOList);
        } catch (NotFoundException e) {
            logger.info("No users found for the project with ID: {}", projectId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("Internal Server Error while retrieving users for the project with ID: " + projectId, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieve a list of users in the project by role.
     *
     * @param projectId The ID of the project to retrieve users from.
     * @param role      The role by which to filter the users.
     * @return ResponseEntity with a list of users filtered by role or an error response.
     */
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
        logger.info("Received a request to retrieve a list of users in the project with ID: {} by role: {}", projectId, role);

        try {
            EnumRole enumRole = EnumRole.valueOf(role.toUpperCase());
            List<User> userList = projectService.getAllUsersByProjectIdAndRole(projectId, enumRole);
            if (userList.isEmpty()) {
                logger.info("No users found for the project with ID: {} by role: {}", projectId, role);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            List<UserDTO> userDTOList = userList.stream()
                    .map(user -> {
                        UserNames usernames = user.getUserNames();
                        String username = (usernames != null) ? usernames.getUsername() : null;
                        return new UserDTO(user.getId(), user.getName(), user.getEmail(), user.getEnumRole(), username);
                    })
                    .toList();

            logger.info("Users retrieved successfully for the project with ID: {} by role: {}", projectId, role);
            return ResponseEntity.ok(userDTOList);
        } catch (Exception e) {
            logger.error("Internal Server Error while retrieving users for the project with ID: " + projectId + " by role: " + role, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Update a project.
     *
     * @param projectId   The ID of the project to update.
     * @param projectDTO  The updated project data.
     * @return ResponseEntity with the updated project details or an error response.
     */
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
        logger.info("Received a request to update a project with ID: {}", projectId);

        try {
            Optional<Project> optionalProject = projectService.getProjectById(projectId);
            if (optionalProject.isPresent()) {
                Project existingProject = optionalProject.get();
                existingProject.setProjectName(projectDTO.getProjectName());
                existingProject.setProjectDescription(projectDTO.getProjectDescription());
                existingProject.setLastUpdated(LocalDateTime.now());
                Project updatedProject = projectService.updateProject(existingProject);
                ProjectDTO updatedProjectDTO = new ProjectDTO(updatedProject.getProjectId(), updatedProject.getProjectName(), updatedProject.getProjectDescription(), updatedProject.getLastUpdated());
                logger.info("Project with ID: {} updated successfully", projectId);
                return new ResponseEntity<>(updatedProjectDTO, HttpStatus.OK);
            } else {
                logger.info("Project with ID - {} not found", projectId);
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Internal Server Error while updating project with ID: " + projectId, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Delete a project (soft delete).
     *
     * @param id The ID of the project to delete.
     * @return ResponseEntity indicating the success or failure of the deletion.
     */
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
        logger.info("Received a request to delete a project with ID: {}", id);

        if (projectService.existsProjectById(id)) {
            boolean checkIfDeleted = projectService.existsByIdIsDeleted(id);
            if (checkIfDeleted) {
                logger.info("Project with ID: {} doesn't exist", id);
                return ResponseEntity.ok("Project doesn't exist");
            }

            boolean isDeleted = projectService.softDeleteProject(id);
            if (isDeleted) {
                logger.info("Deleted project with ID: {} successfully", id);
                return ResponseEntity.ok("Deleted project successfully");
            } else {
                logger.info("Project with ID: {} not found", id);
                return ResponseEntity.notFound().build();
            }
        } else {
            logger.info("Project with ID: {} not found", id);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Add a user to the project.
     *
     * @param projectId The ID of the project.
     * @param userId    The ID of the user to add to the project.
     * @return ResponseEntity indicating the success or failure of adding the user to the project.
     */
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
        logger.info("Received a request to add user with ID: {} to project with ID: {}", userId, projectId);

        try {
            ResponseEntity<Object> response = projectService.addUserToProjectByUserIdAndProjectId(projectId, userId);
            logger.info("User with ID: {} added to project with ID: {} successfully", userId, projectId);
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        } catch (NotFoundException e) {
            logger.error("Resource not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Resource not found");
        } catch (ConflictException e) {
            logger.error("Conflict - User already exists in the project: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User already exists in the project");
        } catch (Exception e) {
            logger.error("Internal Server Error : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
        }
    }

    /**
     * Remove a user from the project.
     *
     * @param projectId The ID of the project.
     * @param userId    The ID of the user to remove from the project.
     * @return ResponseEntity indicating the success or failure of removing the user from the project.
     */
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
        logger.info("Received a request to remove user with ID: {} from project with ID: {}", userId, projectId);

        try {
            ResponseEntity<String> response = projectService.removeUserFromProjectByUserIdAndProjectId(projectId, userId);
            logger.info("User with ID: {} removed from project with ID: {} successfully", userId, projectId);
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        } catch (NotFoundException e) {
            logger.error("Resource not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Resource not found");
        } catch (Exception e) {
            logger.error("Internal Server Error - {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
        }
    }

    /**
     * Remove a user from the project and repository.
     *
     * @param projectId      The ID of the project.
     * @param userId         The ID of the user to remove from the project and repository.
     * @param collaboratorDTO The collaborator information needed for repository access.
     * @return ResponseEntity indicating the success or failure of removing the user from the project and repository.
     */
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
        logger.info("Received a request to remove user with ID: {} from project with ID: {} and repo", userId, projectId);

        try {
            ResponseEntity<String> response = projectService.removeUserFromProjectAndRepo(projectId, userId, collaboratorDTO);
            if (response.getStatusCode() == HttpStatus.BAD_REQUEST) {
                logger.error("Unable to remove user from project and repo: {}", response.getBody());
                return ResponseEntity.badRequest().body("Unable to remove user");
            } else {
                logger.info("User with ID: {} removed from project with ID: {} and repo successfully", userId, projectId);
                return ResponseEntity.ok("User removed successfully");
            }
        } catch (NotFoundException e) {
            logger.error("Project or User not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Project or User not found");
        } catch (Exception e) {
            logger.error("Internal Server Error Occurred: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Retrieve users by role in the project.
     *
     * @param projectId The ID of the project.
     * @param role      The role for which users need to be retrieved.
     * @return ResponseEntity containing the list of users retrieved based on the specified role.
     */
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
        logger.info("Received a request to get users by role: {} in project with ID: {}", role, projectId);

        List<UserDTO> userDTOList = projectService.getUsersByProjectIdAndRole(projectId, role);
        logger.info("Users by role: {} retrieved successfully in project with ID: {}", role, projectId);
        return new ResponseEntity<>(userDTOList, HttpStatus.OK);
    }

    /**
     * Add a repository to the project based on Project ID and Repo ID.
     *
     * @param projectId The ID of the project.
     * @param repoId    The ID of the repository to be added to the project.
     * @return ResponseEntity indicating the result of the operation.
     */
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
        logger.info("Received a request to add repository with ID: {} to the project with ID: {}", repoId, projectId);

        try {
            ResponseEntity<Object> result = projectService.addRepositoryToProject(projectId, repoId);
            if (result.getStatusCode() == HttpStatus.NOT_FOUND) {
                logger.error("Repository or Project not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Repository or Project not found");
            } else {
                logger.info("Repository added to project successfully");
                return ResponseEntity.ok("Stored successfully");
            }
        } catch (Exception e) {
            logger.error("Internal Server Error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Retrieve projects without Figma URL.
     *
     * @return ResponseEntity containing the list of projects without Figma URL.
     */
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
        logger.info("Received a request to retrieve projects without Figma URL");

        List<ProjectDTO> projects = projectService.getProjectsWithoutFigmaURL();
        logger.info("Projects without Figma URL retrieved successfully");
        return ResponseEntity.ok(projects);
    }

    /**
     * Retrieve projects without Google Drive Link.
     *
     * @return ResponseEntity containing the list of projects without Google Drive Link.
     */
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
        logger.info("Received a request to retrieve projects without Google Drive Link");

        List<ProjectDTO> projects = projectService.getProjectsWithoutGoogleDriveLink();
        logger.info("Projects without Google Drive Link retrieved successfully");
        return ResponseEntity.ok(projects);
    }

    /**
     * Count people by project name.
     *
     * @return ResponseEntity containing the people count for each project name.
     */
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
        logger.info("Received a request to count people by project name");

        List<ProjectNamePeopleCountDTO> peopleCountDTOs = projectService.getCountAllPeopleAndProjectName();
        if (peopleCountDTOs.isEmpty()) {
            logger.info("No content : Empty result");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Empty");
        } else {
            logger.info("People count by project name retrieved successfully");
            return ResponseEntity.ok(peopleCountDTOs);
        }
    }

    /**
     * Count all projects.
     *
     * @return ResponseEntity containing the count of all projects.
     */
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
        logger.info("Received a request to count all projects");

        Integer countProjects = projectService.getCountAllProjects();
        if (countProjects == 0) {
            logger.info("No content : Empty result found");
            return ResponseEntity.ok(0);
        } else {
            logger.info("Projects count is retrieved successfully");
            return ResponseEntity.ok(countProjects);
        }
    }

    /**
     * Count all projects by role.
     *
     * @param role The role to filter projects.
     * @return ResponseEntity containing the count of projects based on the specified role.
     */
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
        logger.info("Received a request to count all projects by role: {}", role);

        EnumRole enumRole = EnumRole.valueOf(role.toUpperCase());
        Integer countProjects = projectService.getCountAllProjectsByRole(enumRole);
        if (countProjects == 0) {
            logger.info("No content found, empty result");
            return ResponseEntity.ok(0);
        } else {
            logger.info("Count retrieved successfully");
            return ResponseEntity.ok(countProjects);
        }
    }

    /**
     * Count all projects by user ID.
     *
     * @param id The ID of the user to filter projects.
     * @return ResponseEntity containing the count of projects based on the specified user ID.
     */
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
        logger.info("Received a request to count all projects by user ID: {}", id);

        Integer countProjects = projectService.getCountAllProjectsByUserId(id);
        if (countProjects == 0) {
            logger.info("No content found : Empty result");
            return ResponseEntity.ok(0);
        } else {
            logger.info("Count of projects retrieved successfully");
            return ResponseEntity.ok(countProjects);
        }
    }

    /**
     * Count all users in a project by project ID.
     *
     * @param projectId The ID of the project to count users.
     * @return ResponseEntity containing the count of users in the specified project.
     */
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
        logger.info("Received a request to count all users in a project by project ID: {}", projectId);

        Integer countUsers = projectService.getCountAllUsersByProjectId(projectId);
        if (countUsers == 0) {
            logger.info("No content found");
            return ResponseEntity.ok(0);
        } else {
            logger.info("Users count retrieved successfully");
            return ResponseEntity.ok(countUsers);
        }
    }

    /**
     * Count all users in a project by role and project ID.
     *
     * @param projectId The ID of the project to count users.
     * @param role      The role by which to filter users.
     * @return ResponseEntity containing the count of users in the specified project with the given role.
     */
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
        logger.info("Received a request to count all users in a project by role and project ID: {}, Role: {}", projectId, role);

        EnumRole enumRole = EnumRole.valueOf(role.toUpperCase());
        Integer countUsers = projectService.getCountAllUsersByProjectIdAndRole(projectId, enumRole);
        if (countUsers == 0) {
            logger.info("No content found : Empty result");
            return ResponseEntity.ok(0);
        } else {
            logger.info("Users count retrieved successfully");
            return ResponseEntity.ok(countUsers);
        }

    }

    /**
     * Count all active projects.
     *
     * @return ResponseEntity containing the count of all active projects.
     */
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
        logger.info("Received a request to count all active projects");

        Integer countProjects = projectService.getCountAllActiveProjects();
        if (countProjects == 0) {
            logger.info("No content - Empty result");
            return ResponseEntity.ok(0);
        } else {
            logger.info("Projects count retrieved successfully");
            return ResponseEntity.ok(countProjects);
        }
    }

    /**
     * Count all inactive projects.
     *
     * @return ResponseEntity containing the count of all inactive projects.
     */
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
        logger.info("Received a request to count all inactive projects");

        Integer countProjects = projectService.getCountAllInActiveProjects();
        if (countProjects == 0) {
            logger.info("No content - Empty result");
            return ResponseEntity.ok(0);
        } else {
            logger.info("Projects count retrieved successfully");
            return ResponseEntity.ok(countProjects);
        }
    }

    /**
     *
     * @param projectId The ID of the project.
     * @return ResponseEntity containing the project details.
     */
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
        logger.info("Received a request to get project details by project ID: {}", projectId);
            try {
                ProjectDTO projectDetails = projectService.getProjectDetailsById(projectId);
                logger.info("Project details retrieved successfully");
                return new ResponseEntity<>(projectDetails, HttpStatus.OK);
            } catch (Exception e) {
                logger.info("Internal Server Error: {}", e.getMessage());
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }

    }
}
