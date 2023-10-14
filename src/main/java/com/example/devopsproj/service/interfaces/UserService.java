package com.example.devopsproj.service.interfaces;

import com.example.devopsproj.commons.enumerations.EnumRole;
import com.example.devopsproj.dto.requestdto.UserCreationDTO;
import com.example.devopsproj.dto.responsedto.ProjectDTO;
import com.example.devopsproj.dto.responsedto.UserDTO;
import com.example.devopsproj.dto.responsedto.UserProjectsDTO;
import com.example.devopsproj.model.User;
import org.springframework.http.ResponseEntity;
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

    List<UserDTO> getAllUsers();

    List<ProjectDTO> getAllProjectsAndRepositoriesByUserId(Long userId);

    ResponseEntity<Object> getProjectsByRoleAndUserId(Long userId, String role);

    UserDTO loginVerification(String email);

    String userLogout(Long id);
}
