package com.example.devopsproj.service.interfaces;

import com.example.devopsproj.commons.enumerations.EnumRole;
import com.example.devopsproj.dto.responsedto.*;
import com.example.devopsproj.model.Project;
import com.example.devopsproj.model.User;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

public interface ProjectService {

    ProjectDTO createProject(ProjectDTO projectDTO);

    Optional<Project> getProjectById(Long id);

    ResponseEntity<Object> getProject(Long id);

    List<ProjectWithUsersDTO> getAllProjectsWithUsers();

    ResponseEntity<Object> addRepositoryToProject(Long projectId, Long repoId);

    List<Project> getAll();

    List<Project> getAllProjects();

    Project updateProject(Project updatedProject);

    //get all users in the project based on project id
    List<UserDTO> getAllUsersByProjectId(Long projectId);

    List<User> getAllUsersByProjectIdAndRole(Long projectId, EnumRole role);

    //to check if the project has already been deleted
    boolean existsByIdIsDeleted(Long id);

    //soft deleting project
    boolean softDeleteProject(Long id);

    boolean existsProjectById(Long id);

    boolean existUserInProject(Long projectId, Long userId);

    Integer getCountAllProjects();

    Integer getCountAllProjectsByRole(EnumRole enumRole);

    Integer getCountAllProjectsByUserId(Long id);

    Integer getCountAllUsersByProjectId(Long projectId);

    List<ProjectNamePeopleCountDTO> getCountAllPeopleAndProjectName();

    Integer getCountAllUsersByProjectIdAndRole(Long projectId, EnumRole enumRole);

    Integer getCountAllActiveProjects();

    Integer getCountAllInActiveProjects();

    List<UserDTO> getUsersByProjectIdAndRole(Long projectId, String role);

    List<ProjectDTO> getProjectsWithoutFigmaURL();

    List<ProjectDTO> getProjectsWithoutGoogleDriveLink();

    ProjectDTO getProjectDetailsById(Long projectId);

    // Add User to Project by User ID and Project ID
    ResponseEntity<Object> addUserToProjectByUserIdAndProjectId(Long projectId, Long userId);

    // Remove User from Project based on User ID and Project ID
    ResponseEntity<String> removeUserFromProjectByUserIdAndProjectId(Long projectId, Long userId);

    // Remove User from Project and Repo based on User ID and Project ID
    ResponseEntity<String> removeUserFromProjectAndRepo(Long projectId, Long userId, CollaboratorDTO collaboratorDTO);
}
