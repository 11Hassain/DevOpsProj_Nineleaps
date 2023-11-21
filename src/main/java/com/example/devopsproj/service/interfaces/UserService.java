package com.example.devopsproj.service.interfaces;

import com.example.devopsproj.commons.enumerations.EnumRole;
import com.example.devopsproj.dto.requestdto.UserCreationDTO;
import com.example.devopsproj.dto.responsedto.ProjectDTO;
import com.example.devopsproj.dto.responsedto.UserDTO;
import com.example.devopsproj.dto.responsedto.UserProjectsDTO;
import com.example.devopsproj.model.Project;
import com.example.devopsproj.model.User;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Optional;
/**
 * Service interface for managing user-related operations, including creation, retrieval, update,
 * deletion, and various counts related to users and their associated projects.
 */

public interface UserService {
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

    List<UserDTO> getUserDTOsByRole(EnumRole role);


    Integer getCountAllUsers();

    Integer getCountAllUsersByRole(EnumRole role);

    Integer getCountAllUsersByProjectId(Long projectId);

    List<UserProjectsDTO> getAllUsersWithProjects();

    List<UserDTO> getAllUsersWithoutProjects(EnumRole role, Long projectId);

    List<UserProjectsDTO> getUsersWithMultipleProjects();

    boolean projectExists(String projectName);

    List<UserDTO> getAllUsers();

    List<ProjectDTO> getAllProjectsAndRepositoriesByUserId(Long userId);
    String deleteUserById(Long userId);


    List<Project> getUsersByRoleAndUserId(Long userId, EnumRole userRole);

    UserDTO loginVerification(String email);

    String userLogout(Long id);
    List<ProjectDTO> getProjectsByRoleIdAndUserId(Long userId, String role);

}