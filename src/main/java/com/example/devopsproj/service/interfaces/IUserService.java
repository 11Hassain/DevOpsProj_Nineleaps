package com.example.devopsproj.service.interfaces;
import com.example.devopsproj.commons.enumerations.EnumRole;
import com.example.devopsproj.dto.requestdto.UserCreationDTO;
import com.example.devopsproj.dto.responsedto.ProjectDTO;
import com.example.devopsproj.dto.responsedto.UserDTO;
import com.example.devopsproj.dto.responsedto.UserProjectsDTO;
import com.example.devopsproj.model.Project;
import com.example.devopsproj.model.User;

import java.util.List;
import java.util.Optional;

public interface IUserService {


        User saveUser(UserCreationDTO userCreationDTO);

        UserDTO updateUser(Long id, UserDTO userDTO);

        boolean existsByIdIsDeleted(Long id);

        boolean softDeleteUser(Long id);

        boolean existsById(Long id);

        List<User> getUsersByRole(EnumRole enumRole);

        User getUserByEmail(String userEmail);

        Integer getCountAllUsers();

        Integer getCountAllUsersByRole(EnumRole role);

        Integer getCountAllUsersByProjectId(Long projectId);

        List<UserProjectsDTO> getAllUsersWithProjects();

        List<UserDTO> getAllUsersWithoutProjects(EnumRole role, Long projectId);

        List<UserProjectsDTO> getUsersWithMultipleProjects();

        List<UserDTO> getAllUsers();

    public List<ProjectDTO> getAllProjectsAndRepositoriesByUserId(Long userId);

    Optional<User> getUserById(Long id);

    List<Project> getUsersByRoleAndUserId(Long userId, EnumRole userRole);


        UserDTO loginVerification(String email);

        String userLogout(Long id);

        User getUserViaPhoneNumber(String phoneNumber);

        User getUserByMail(String userMail);
    }

