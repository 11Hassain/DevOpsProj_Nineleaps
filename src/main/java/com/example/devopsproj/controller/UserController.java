package com.example.devopsproj.controller;


import com.example.devopsproj.commons.enumerations.EnumRole;
import com.example.devopsproj.dto.responsedto.*;
import com.example.devopsproj.dto.requestdto.UserCreationDTO;
import com.example.devopsproj.model.User;

import com.example.devopsproj.service.implementations.UserServiceImpl;

import com.example.devopsproj.service.interfaces.UserService;
import io.swagger.annotations.ApiOperation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
/**
 * Controller for managing user-related operations, including user creation, retrieval, and updates.
 */
@RestController
@RequestMapping("/api/v1/users")
@Validated
@RequiredArgsConstructor
public class UserController {

    private final UserService userServiceImpl;



    /**
     * Save a user.
     *
     * @param userCreationDTO The user details to be saved.
     * @return ResponseEntity indicating the result of the user creation operation.
     */
    @PostMapping("/")
    @ApiOperation("Save a user")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> saveUser(@Valid @RequestBody UserCreationDTO userCreationDTO) {
        User savedUser = userServiceImpl.saveUser(userCreationDTO);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    /**
     * Find a user by user ID.
     *
     * @param userId The ID of the user to be retrieved.
     * @return ResponseEntity containing the retrieved user's details or a "Not Found" response.
     */
    @GetMapping("/{user_id}")
    @ApiOperation("Find a user by user ID")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getUserById(@PathVariable Long userId) {
        Optional<User> optionalUser = userServiceImpl.getUserById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            UserDTO userDTO = new UserDTO(user.getId(), user.getName(), user.getEmail(), user.getEnumRole(), user.getLastUpdated(), user.getLastLogout());
            return new ResponseEntity<>(userDTO, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Get projects by user ID and role.
     *
     * @param userId The ID of the user.
     * @param role   The role of the user.
     * @return ResponseEntity containing a list of projects associated with the specified user and role.
     */
    @GetMapping("/{id}/role/{role}/projects")
    @ApiOperation("Get projects by user ID and role")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getProjectsByRoleIdAndUserId(
            @PathVariable("id") Long userId,
            @PathVariable("role") String role) {
        List<ProjectDTO> projectDTOList = userServiceImpl.getProjectsByRoleIdAndUserId(userId, role);
        if (projectDTOList.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(projectDTOList, HttpStatus.OK);
    }

    /**
     * Update a user by ID.
     *
     * @param id     The ID of the user to be updated.
     * @param userDTO The updated user details.
     * @return ResponseEntity containing the updated user details or an "OK" response.
     */
    @PutMapping("/update/{id}")
    @ApiOperation("Update user by ID")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> updateUser(@PathVariable("id") Long id, @Valid @RequestBody UserDTO userDTO) {
        UserDTO updatedUserDTO = userServiceImpl.updateUser(id, userDTO);
        return new ResponseEntity<>(updatedUserDTO, HttpStatus.OK);
    }

    /**
     * Soft-delete a user by user ID.
     *
     * @param userId The ID of the user to be soft-deleted.
     * @return ResponseEntity indicating the result of the user deletion operation.
     */
    @DeleteMapping("/delete/{user_id}")
    @ApiOperation("Soft-delete user")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> deleteUserById(@PathVariable Long userId) {
        String deletionResult = userServiceImpl.deleteUserById(userId);
        return ResponseEntity.ok(deletionResult);
    }

    /**
     * Get users by role.
     *
     * @param role The role of the users to be retrieved.
     * @return ResponseEntity containing a list of users with the specified role.
     */
    @GetMapping("/role/{role}")
    @ApiOperation("Get users by role")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getUserByRoleId(@PathVariable("role") String role) {
        EnumRole userRole = EnumRole.valueOf(role.toUpperCase());
        List<UserDTO> userDTOList = userServiceImpl.getUserDTOsByRole(userRole);
        return new ResponseEntity<>(userDTOList, HttpStatus.OK);
    }

    /**
     * Get the count of all users.
     *
     * @return ResponseEntity containing the count of all users.
     */
    @GetMapping("/count")
    @ApiOperation("Get count of all users")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getCountAllUsers() {
        Integer countUsers = userServiceImpl.getCountAllUsers();
        if (countUsers == 0) {
            return ResponseEntity.ok(0);
        } else {
            return ResponseEntity.ok(countUsers);
        }
    }



    /**
     * Get the count of users by role.
     *
     * @param role The role of the users to be counted.
     * @return ResponseEntity containing the count of users with the specified role.
     */
    @GetMapping("/count/{role}")
    @ApiOperation("Get count of users by role")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getCountAllUsersByRole(@PathVariable String role) {
        EnumRole userRole = EnumRole.valueOf(role.toUpperCase());
        Integer countUsersByRole = userServiceImpl.getCountAllUsersByRole(userRole);
        if (countUsersByRole == 0) {
            return ResponseEntity.ok(0);
        } else {
            return ResponseEntity.ok(countUsersByRole);
        }
    }


    /**
     * Get the count of users by project ID.
     *
     * @param projectId The ID of the project for which users are to be counted.
     * @return ResponseEntity containing the count of users associated with the specified project.
     */
    @GetMapping("/count/project/{projectId}")
    @ApiOperation("Get count of users by project ID")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getCountAllUsersByProjectId(@PathVariable Long projectId) {
        Integer countUsersByProject = userServiceImpl.getCountAllUsersByProjectId(projectId);
        if (countUsersByProject == 0) {
            return ResponseEntity.ok(0);
        } else {
            return ResponseEntity.ok(countUsersByProject);
        }
    }

    /**
     * Get all projects and repositories by user ID.
     *
     * @param id The ID of the user for which projects and repositories are to be retrieved.
     * @return ResponseEntity containing a list of projects and repositories associated with the specified user.
     */
    @GetMapping("/{id}/projects")
    @ApiOperation("Get all projects and repositories by user ID")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getAllProjectsByUserId(@PathVariable Long id) {
        List<ProjectDTO> projects = userServiceImpl.getAllProjectsAndRepositoriesByUserId(id);
        return ResponseEntity.ok(projects);
    }

    /**
     * Get all users.
     *
     * @return ResponseEntity containing a list of all users.
     */
    @GetMapping("/get")
    @ApiOperation("Get all users")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getAllUsers() {
        return ResponseEntity.ok(userServiceImpl.getAllUsers());
    }


    /**
     * Get all users with their associated projects.
     *
     * @return ResponseEntity containing a list of users with their associated projects.
     */
    @GetMapping("/getAll")
    @ApiOperation("Get all users with projects")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getAllUsersWithProjects() {
        List<UserProjectsDTO> userProjectsDTOs = userServiceImpl.getAllUsersWithProjects();
        return ResponseEntity.ok(userProjectsDTOs);
    }


    /**
     * Get users with multiple projects.
     *
     * @return ResponseEntity containing a list of users with multiple projects.
     */
    @GetMapping("/getMultiple")
    @ApiOperation("Get users with multiple projects")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getUsersWithMultipleProjects() {
        List<UserProjectsDTO> usersWithMultipleProjects = userServiceImpl.getUsersWithMultipleProjects();
        return ResponseEntity.ok(usersWithMultipleProjects);
    }

    /**
     * Get users without a specific project by role and project ID.
     *
     * @param role     The role of the users to be retrieved.
     * @param projectId The ID of the project to exclude users from.
     * @return ResponseEntity containing a list of users with the specified role without the specified project.
     */
    @GetMapping("/withoutProject")
    @ApiOperation("Get users without a specific project by role and project ID")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getUserWithoutProject(
            @RequestParam("role") String role,
            @RequestParam("projectId") Long projectId) {
        EnumRole enumRole = EnumRole.valueOf(role.toUpperCase());
        List<UserDTO> userDTOList = userServiceImpl.getAllUsersWithoutProjects(enumRole, projectId);
        return ResponseEntity.status(HttpStatus.OK).body(userDTOList);
    }

    /**
     * Logout a user by ID.
     *
     * @param id The ID of the user to be logged out.
     * @return ResponseEntity containing a response message.
     */
    @PostMapping("/{userId}/logout")
    @ApiOperation("User logout by user ID")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> userLogout(@PathVariable("userId") Long id) {
        String response = userServiceImpl.userLogout(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}