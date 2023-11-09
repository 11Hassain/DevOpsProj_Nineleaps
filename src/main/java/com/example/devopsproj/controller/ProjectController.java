package com.example.devopsproj.controller;

import com.example.devopsproj.commons.enumerations.EnumRole;
import com.example.devopsproj.dto.responsedto.*;

import com.example.devopsproj.service.interfaces.ProjectService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
/**
 * Controller for managing projects.
 */
@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;


    /**
     * Create a new project and return its details.
     *
     * @param projectDTO The project details to create.
     * @return ResponseEntity containing the created project details.
     */
    @PostMapping("/create")
    @ApiOperation("Create a new project and return its details")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> createProject(@RequestBody ProjectDTO projectDTO) {
        // Create the project and return its details in the response.
        ProjectDTO createdProjectDTO = projectService.createProject(projectDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProjectDTO);
    }

    /**
     * Get project details by ID.
     *
     * @param id The ID of the project to retrieve.
     * @return ResponseEntity containing the project details, or a NOT_FOUND response if not found.
     */
    @GetMapping("/{id}")
    @ApiOperation("Get project details by ID")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getProjectById(@PathVariable("id") Long id) {
        ProjectDTO projectDTO = projectService.getProjectById(id);
        if (projectDTO != null) {
            return new ResponseEntity<>(projectDTO, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Get a list of all projects.
     *
     * @param page The page number for pagination (default: 0).
     * @param size The number of items per page (default: 10).
     * @return ResponseEntity containing a list of projects or a NOT_FOUND response if no projects are found.
     */
    @GetMapping("/all")
    @ApiOperation("Get a list of all projects")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getAll(@RequestParam(value = "page", defaultValue = "0") int page,
                                         @RequestParam(value = "size", defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ProjectDTO> projectDTOPage = projectService.getAll(pageable);

        if (projectDTOPage.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No projects found");
        }

        return ResponseEntity.status(HttpStatus.OK).body(projectDTOPage.getContent());
    }


    /**
     * Get a list of all projects along with associated users.
     *
     * @param page The page number for pagination (default: 0).
     * @param size The number of items per page (default: 10).
     * @return ResponseEntity containing a list of projects with associated users or a NOT_FOUND response if none are found.
     */
    @GetMapping("/allProjects")
    @ApiOperation("Get a list of all projects along with associated users")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getAllProjectsWithUsers(@RequestParam(value = "page", defaultValue = "0") int page,
                                                          @RequestParam(value = "size", defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ProjectWithUsersDTO> projectWithUsersPage = projectService.getAllProjectsWithUsers(pageable);

        if (projectWithUsersPage.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No projects with users found");
        }

        return ResponseEntity.status(HttpStatus.OK).body(projectWithUsersPage.getContent());
    }


    /**
     * Get a list of users in a specific project.
     *
     * @param projectId The ID of the project to retrieve users for.
     * @return ResponseEntity containing a list of users in the specified project.
     */
    @GetMapping("/{projectId}/users")
    @ApiOperation("Get a list of users in a specific project")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getAllUsersByProjectId(@PathVariable Long projectId) {
        List<UserDTO> userDTOList = projectService.getAllUsersByProjectId(projectId);
        return ResponseEntity.ok(userDTOList);
    }


    /**
     * Get a list of all users in a project by their role.
     *
     * @param projectId The ID of the project to retrieve users from.
     * @param role      The role of users to filter by.
     * @return ResponseEntity containing a list of users in the specified project with the given role.
     */
    @GetMapping("/{projectId}/users/{role}")
    @ApiOperation("Get a list of all users in a project by their role")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getAllUsersByProjectIdByRole(
            @PathVariable Long projectId,
            @PathVariable String role) {
        EnumRole enumRole = EnumRole.valueOf(role.toUpperCase());

        List<UserDTO> userDTOList = projectService.getAllUsersByProjectIdAndRole(projectId, enumRole);
        return ResponseEntity.ok(userDTOList);
    }

    /**
     * Update an existing project.
     *
     * @param projectId  The ID of the project to update.
     * @param projectDTO The updated project details.
     * @return ResponseEntity containing the updated project details.
     */
    @PutMapping("/update/{projectId}")
    @ApiOperation("Update an existing project")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ProjectDTO> updateProject(@PathVariable("projectId") Long projectId,
                                                    @RequestBody ProjectDTO projectDTO) {
        ProjectDTO updatedProjectDTO = projectService.updateProject(projectId, projectDTO);
        return new ResponseEntity<>(updatedProjectDTO, HttpStatus.OK);
    }




    /**
     * Soft delete a project.
     *
     * @param id The ID of the project to soft delete.
     * @return ResponseEntity with a success message.
     */
    @DeleteMapping("/delete/{id}")
    @ApiOperation("Soft delete a project")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> deleteProject(@PathVariable("id") Long id) {
        return projectService.deleteProject(id);
    }

    /**
     * Add a user to a project.
     *
     * @param projectId The ID of the project.
     * @param userId    The ID of the user to add.
     * @return ResponseEntity with a success message.
     */
    @PutMapping("/{projectId}/users/{userId}")
    @ApiOperation("Add a user to a project")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> addUserToProject(
            @PathVariable("projectId") Long projectId,
            @PathVariable("userId") Long userId) {
        return projectService.addUserToProject(projectId, userId);
    }

    /**
     * Remove a user from a project.
     *
     * @param projectId The ID of the project.
     * @param userId    The ID of the user to remove.
     * @return ResponseEntity with a success message.
     */
    @DeleteMapping("/{projectId}/users/{userId}")
    @ApiOperation("Remove a user from a project")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> removeUserFromProject(
            @PathVariable("projectId") Long projectId,
            @PathVariable("userId") Long userId) {
        return projectService.removeUserFromProject(projectId, userId);
    }

    /**
     * Remove a user from a project and their associated repository.
     *
     * @param projectId        The ID of the project.
     * @param userId           The ID of the user to remove.
     * @param collaboratorDTO  Details of the user to be removed.
     * @return ResponseEntity with a success message.
     */
    @DeleteMapping("/{projectId}/users/{userId}/repo")
    @ApiOperation("Remove a user from a project and their associated repository")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> removeUserFromProjectAndRepo(
            @PathVariable("projectId") Long projectId,
            @PathVariable("userId") Long userId,
            @RequestBody CollaboratorDTO collaboratorDTO) {
        return projectService.removeUserFromProjectAndRepo(projectId, userId, collaboratorDTO);
    }


    /**
     * Get users by project ID and role.
     *
     * @param projectId The ID of the project.
     * @param role      The role of the users to retrieve.
     * @return ResponseEntity containing a list of users.
     */
    @GetMapping("/{projectId}/users/role/{role}")
    @ApiOperation("Get users by project ID and role")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<UserDTO>> getUsersByProjectIdAndRole(
            @PathVariable("projectId") Long projectId,
            @PathVariable("role") String role) {
        List<UserDTO> userDTOList = projectService.getUsersByProjectIdAndRole(projectId, role);
        return new ResponseEntity<>(userDTOList, HttpStatus.OK);
    }


    /**
     * Add a repository to a project.
     *
     * @param projectId The ID of the project.
     * @param repoId    The ID of the repository to add.
     * @return ResponseEntity with a success message or details.
     */
    @PutMapping("/{projectId}/repository/{repoId}")
    @ApiOperation("Add a repository to a project")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> addRepositoryToProject(
            @PathVariable("projectId") Long projectId,
            @PathVariable("repoId") Long repoId) {
        return projectService.addRepositoryToProject(projectId, repoId);
    }

    /**
     * Get projects without a Figma URL.
     *
     * @return ResponseEntity containing a list of projects without a Figma URL.
     */
    @GetMapping("/without-figma-url")
    @ApiOperation("Get projects without a Figma URL")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<ProjectDTO>> getProjectsWithoutFigmaURL() {
        List<ProjectDTO> projectsWithoutFigmaURL = projectService.getProjectsWithoutFigmaURL();
        return ResponseEntity.ok(projectsWithoutFigmaURL);
    }

    /**
     * Get projects without a Google Drive link.
     *
     * @return ResponseEntity containing a list of projects without a Google Drive link.
     */
    @GetMapping("/without-google-drive")
    @ApiOperation("Get projects without a Google Drive link")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<ProjectDTO>> getProjectsWithoutGoogleDriveLink() {
        List<ProjectDTO> projectsWithoutGoogleDriveLink = projectService.getProjectsWithoutGoogleDriveLink();
        return ResponseEntity.ok(projectsWithoutGoogleDriveLink);
    }

    /**
     * Count all people by project ID and name.
     *
     * @return ResponseEntity containing a list of people count by project ID and name.
     */
    @GetMapping("/countPeople")
    @ApiOperation("Count all people by project ID and name")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<ProjectNamePeopleCountDTO>> countAllPeopleByProjectIdAndName() {
        List<ProjectNamePeopleCountDTO> peopleCountDTOs = projectService.getCountAllPeopleAndProjectName();
        return ResponseEntity.ok(peopleCountDTOs);
    }
    /**
     * Count all projects.
     *
     * @return ResponseEntity containing the count of all projects.
     */
    @GetMapping("/count")
    @ApiOperation("Count all projects")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> countAllProjects() {
        Integer countProjects = projectService.getCountAllProjects();
        return ResponseEntity.ok(countProjects);
    }


    /**
     * Count all projects by role.
     *
     * @param role The role to count projects by.
     * @return ResponseEntity containing the count of projects for the specified role.
     */
    @GetMapping("/count/role/{role}")
    @ApiOperation("Count all projects by role")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> countAllProjectsByRole(@PathVariable("role") String role) {
        EnumRole enumRole = EnumRole.valueOf(role.toUpperCase());
        Integer countProjects = projectService.getCountAllProjectsByRole(enumRole);
        return ResponseEntity.ok(countProjects);
    }

    /**
     * Count all projects by user ID.
     *
     * @param id The ID of the user to count projects for.
     * @return ResponseEntity containing the count of projects for the specified user.
     */
    @GetMapping("/count/user/{userId}")
    @ApiOperation("Count all projects by user ID")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> countAllProjectsByUserId(@PathVariable("userId") Long id) {
        Integer countProjects = projectService.getCountAllProjectsByUserId(id);
        return ResponseEntity.ok(countProjects);
    }
    /**
     * Count all users by project ID.
     *
     * @param projectId The ID of the project to count users for.
     * @return ResponseEntity containing the count of users for the specified project.
     */
    @GetMapping("/{projectId}/count")
    @ApiOperation("Count all users by project ID")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> countAllUsersByProjectId(@PathVariable Long projectId) {
        Integer countUsers = projectService.getCountAllUsersByProjectId(projectId);
        return ResponseEntity.ok(countUsers);
    }

    /**
     * Count all users by project ID and role.
     *
     * @param projectId The ID of the project to count users for.
     * @param role      The role to count users by.
     * @return ResponseEntity containing the count of users for the specified project and role.
     */
    @GetMapping("/{projectId}/count/{role}")
    @ApiOperation("Count all users by project ID and role")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> countAllUsersByProjectIdByRole(
            @PathVariable Long projectId,
            @PathVariable String role) {
        EnumRole enumRole = EnumRole.valueOf(role.toUpperCase());
        Integer countUsers = projectService.getCountAllUsersByProjectIdAndRole(projectId, enumRole);
        return ResponseEntity.ok(countUsers);
    }



    /**
     * Count all active projects.
     *
     * @return ResponseEntity containing the count of active projects.
     */
    @GetMapping("/count/active")
    @ApiOperation("Count all active projects")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> countAllActiveProjects() {
        Integer countProjects = projectService.getCountAllActiveProjects();
        return ResponseEntity.ok(countProjects);
    }


    /**
     * Count all inactive projects.
     *
     * @return ResponseEntity containing the count of inactive projects.
     */
    @GetMapping("/count/inactive")
    @ApiOperation("Count all inactive projects")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> countAllInActiveProjects() {
        Integer countProjects = projectService.getCountAllInActiveProjects();
        return ResponseEntity.ok(countProjects);
    }


    /**
     * Get project details by ID.
     *
     * @param projectId The ID of the project to retrieve details for.
     * @return ResponseEntity containing the project details or a not found response.
     */
    @GetMapping("/{projectId}/details")
    @ApiOperation("Get project details by ID")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getProjectDetailsById(@PathVariable Long projectId) {
        ProjectDTO projectDetails = projectService.getProjectDetailsById(projectId);

        if (projectDetails != null) {
            return new ResponseEntity<>(projectDetails, HttpStatus.OK);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


}
