package com.example.devopsproj.service.interfaces;

import com.example.devopsproj.commons.enumerations.EnumRole;
import com.example.devopsproj.dto.requestDto.UserCreationDTO;
import com.example.devopsproj.dto.responseDto.ProjectDTO;
import com.example.devopsproj.dto.responseDto.UserDTO;
import com.example.devopsproj.dto.responseDto.UserProjectsDTO;
import com.example.devopsproj.model.Project;
import com.example.devopsproj.model.User;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Optional;

public interface UserService {
    //implementing user creation using DTO pattern
    User saveUser(@RequestBody UserCreationDTO userCreationDTO);

    UserDTO updateUser(Long id, UserDTO userDTO);

    //find user by user id
    Optional<User> getUserById(Long id);

    //this function says whether id is soft-deleted or not
    boolean existsByIdIsDeleted(Long id);

    //Soft deleting the user
    boolean softDeleteUser(Long id);

    boolean existsById(Long id);

    //get all user based on role id
    List<User> getUsersByRole(EnumRole enumRole);

    Integer getCountAllUsers();

    Integer getCountAllUsersByRole(EnumRole role);

    Integer getCountAllUsersByProjectId(Long projectId);

    List<UserProjectsDTO> getAllUsersWithProjects();

    List<UserDTO> getAllUsersWithoutProjects(EnumRole role, Long projectId);

    List<UserProjectsDTO> getUsersWithMultipleProjects();

    boolean projectExists(String projectName);

    List<UserDTO> getAllUsers();

    List<ProjectDTO> getAllProjectsAndRepositoriesByUserId(Long userId);

    List<Project> getUsersByRoleAndUserId(Long userId, EnumRole userRole);

    UserDTO loginVerification(String email);

    String userLogout(Long id);
}