package com.example.devopsproj.controller;


import com.example.devopsproj.commons.enumerations.EnumRole;
import com.example.devopsproj.dto.responsedto.*;
import com.example.devopsproj.dto.requestdto.UserCreationDTO;
import com.example.devopsproj.model.User;

import com.example.devopsproj.service.implementations.UserServiceImpl;

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


    @PostMapping("/") // Save the user
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> saveUser(@Valid @RequestBody UserCreationDTO userCreationDTO) {
        User savedUser = userServiceImpl.saveUser(userCreationDTO);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    @GetMapping("/{user_id}") // Find user by user id
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
    @GetMapping("/{id}/role/{role}/projects")
    public ResponseEntity<Object> getProjectsByRoleIdAndUserId(
            @PathVariable("id") Long userId,
            @PathVariable("role") String role) {
        List<ProjectDTO> projectDTOList = userServiceImpl.getProjectsByRoleIdAndUserId(userId, role);
        if (projectDTOList.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(projectDTOList, HttpStatus.OK);
    }

    @PutMapping("/update/{id}") // Update user by id
    public ResponseEntity<Object> updateUser(@PathVariable("id") Long id, @Valid @RequestBody UserDTO userDTO) {
        UserDTO updatedUserDTO = userServiceImpl.updateUser(id, userDTO);
        return new ResponseEntity<>(updatedUserDTO, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{user_id}") // Soft-deleting user
    public ResponseEntity<String> deleteUserById(@PathVariable Long userId) {
        String deletionResult = userServiceImpl.deleteUserById(userId);
        return ResponseEntity.ok(deletionResult);
    }



    @GetMapping("/role/{role}")
    public ResponseEntity<Object> getUserByRoleId(@PathVariable("role") String role) {
        EnumRole userRole = EnumRole.valueOf(role.toUpperCase());
        List<UserDTO> userDTOList = userServiceImpl.getUserDTOsByRole(userRole);
        return new ResponseEntity<>(userDTOList, HttpStatus.OK);
    }

    @GetMapping("/count") // Get count of all the users
    public ResponseEntity<Object> getCountAllUsers() {
        Integer countUsers = userServiceImpl.getCountAllUsers();
        if (countUsers == 0) {
            return ResponseEntity.ok(0);
        } else {
            return ResponseEntity.ok(countUsers);
        }
    }


    @GetMapping("/count/{role}") // Get count of users by role
    public ResponseEntity<Object> getCountAllUsersByRole(@PathVariable String role) {
        EnumRole userRole = EnumRole.valueOf(role.toUpperCase());
        Integer countUsersByRole = userServiceImpl.getCountAllUsersByRole(userRole);
        if (countUsersByRole == 0) {
            return ResponseEntity.ok(0);
        } else {
            return ResponseEntity.ok(countUsersByRole);
        }
    }

    @GetMapping("/count/project/{projectId}") // Get count of users by project ID
    public ResponseEntity<Object> getCountAllUsersByProjectId(@PathVariable Long projectId) {
        Integer countUsersByProject = userServiceImpl.getCountAllUsersByProjectId(projectId);
        if (countUsersByProject == 0) {
            return ResponseEntity.ok(0);
        } else {
            return ResponseEntity.ok(countUsersByProject);
        }
    }

    @GetMapping("/{id}/projects")
    public ResponseEntity<Object> getAllProjectsByUserId(@PathVariable Long id) {
        List<ProjectDTO> projects = userServiceImpl.getAllProjectsAndRepositoriesByUserId(id);
        return ResponseEntity.ok(projects);
    }


    @GetMapping("/get")
    public ResponseEntity<Object> getAllUsers() {
        return ResponseEntity.ok(userServiceImpl.getAllUsers());
    }

    @GetMapping("/getAll")
    public ResponseEntity<Object> getAllUsersWithProjects() {
        List<UserProjectsDTO> userProjectsDTOs = userServiceImpl.getAllUsersWithProjects();
        return ResponseEntity.ok(userProjectsDTOs);
    }

    @GetMapping("/getMultiple")
    public ResponseEntity<Object> getUsersWithMultipleProjects() {
        List<UserProjectsDTO> usersWithMultipleProjects = userServiceImpl.getUsersWithMultipleProjects();
        return ResponseEntity.ok(usersWithMultipleProjects);
    }

    @GetMapping("/withoutProject")
    public ResponseEntity<Object> getUserWithoutProject(
            @RequestParam("role") String role,
            @RequestParam("projectId") Long projectId) {
        EnumRole enumRole = EnumRole.valueOf(role.toUpperCase());
        List<UserDTO> userDTOList = userServiceImpl.getAllUsersWithoutProjects(enumRole, projectId);
        return ResponseEntity.status(HttpStatus.OK).body(userDTOList);
    }

    @PostMapping("/{userId}/logout")
    public ResponseEntity<String> userLogout(@PathVariable("userId") Long id) {
        String response = userServiceImpl.userLogout(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}