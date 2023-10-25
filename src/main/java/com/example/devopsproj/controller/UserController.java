package com.example.devopsproj.controller;


import com.example.devopsproj.commons.enumerations.EnumRole;
import com.example.devopsproj.dto.responsedto.*;
import com.example.devopsproj.dto.requestdto.UserCreationDTO;
import com.example.devopsproj.model.User;

import com.example.devopsproj.service.implementations.UserServiceImpl;

import io.swagger.annotations.ApiOperation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/users")
@Validated
@RequiredArgsConstructor
public class UserController {

    private final UserServiceImpl userServiceImpl;


    // Save a user.
    @PostMapping("/")
    @ApiOperation("Save a user")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> saveUser(@Valid @RequestBody UserCreationDTO userCreationDTO) {
        User savedUser = userServiceImpl.saveUser(userCreationDTO);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    // Find a user by user ID.
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

    // Get projects by user ID and role.
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

    // Update a user by ID.
    @PutMapping("/update/{id}")
    @ApiOperation("Update user by ID")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> updateUser(@PathVariable("id") Long id, @Valid @RequestBody UserDTO userDTO) {
        UserDTO updatedUserDTO = userServiceImpl.updateUser(id, userDTO);
        return new ResponseEntity<>(updatedUserDTO, HttpStatus.OK);
    }

    // Soft-delete a user.
    @DeleteMapping("/delete/{user_id}")
    @ApiOperation("Soft-delete user")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> deleteUserById(@PathVariable Long userId) {
        String deletionResult = userServiceImpl.deleteUserById(userId);
        return ResponseEntity.ok(deletionResult);
    }

    // Get users by role.
    @GetMapping("/role/{role}")
    @ApiOperation("Get users by role")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getUserByRoleId(@PathVariable("role") String role) {
        EnumRole userRole = EnumRole.valueOf(role.toUpperCase());
        List<UserDTO> userDTOList = userServiceImpl.getUserDTOsByRole(userRole);
        return new ResponseEntity<>(userDTOList, HttpStatus.OK);
    }

    // Get the count of all users.
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



    // Get the count of users by role.
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

    // Get the count of users by project ID.
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

    // Get all projects and repositories by user ID.
    @GetMapping("/{id}/projects")
    @ApiOperation("Get all projects and repositories by user ID")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getAllProjectsByUserId(@PathVariable Long id) {
        List<ProjectDTO> projects = userServiceImpl.getAllProjectsAndRepositoriesByUserId(id);
        return ResponseEntity.ok(projects);
    }

    // Get all users.
    @GetMapping("/get")
    @ApiOperation("Get all users")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getAllUsers() {
        return ResponseEntity.ok(userServiceImpl.getAllUsers());
    }

    // Get all users with their associated projects.
    @GetMapping("/getAll")
    @ApiOperation("Get all users with projects")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getAllUsersWithProjects() {
        List<UserProjectsDTO> userProjectsDTOs = userServiceImpl.getAllUsersWithProjects();
        return ResponseEntity.ok(userProjectsDTOs);
    }


    // Get users with multiple projects.
    @GetMapping("/getMultiple")
    @ApiOperation("Get users with multiple projects")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getUsersWithMultipleProjects() {
        List<UserProjectsDTO> usersWithMultipleProjects = userServiceImpl.getUsersWithMultipleProjects();
        return ResponseEntity.ok(usersWithMultipleProjects);
    }

    // Get users without a specific project by role and project ID.
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

    // Logout a user by ID.
    @PostMapping("/{userId}/logout")
    @ApiOperation("User logout by user ID")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> userLogout(@PathVariable("userId") Long id) {
        String response = userServiceImpl.userLogout(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}