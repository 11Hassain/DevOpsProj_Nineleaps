package com.example.devopsproj.service.interfaces;

import com.example.devopsproj.commons.enumerations.EnumRole;
import com.example.devopsproj.dto.responsedto.*;
import com.example.devopsproj.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ProjectService {

    ProjectDTO createProject(ProjectDTO projectDTO);

    ProjectDTO getProjectById(Long id);

    Page<ProjectDTO> getAll(Pageable pageable);


    List<Project> getAllProjects();

    Page<ProjectWithUsersDTO> getAllProjectsWithUsers(Pageable pageable);



    Project updateProject(Project updatedProject);

    //    List<User> getAllUsersByProjectId(Long projectId); // Add this method
    List<UserDTO> getAllUsersByProjectId(Long projectId);

    List<UserDTO> getAllUsersByProjectIdAndRole(Long projectId, EnumRole role);

    ProjectDTO updateProject(Long projectId, ProjectDTO projectDTO);

    ResponseEntity<String> deleteProject(Long id);

    ResponseEntity<Object> addUserToProject(Long projectId, Long userId);

    ResponseEntity<String> removeUserFromProject(Long projectId, Long userId);

    ResponseEntity<String> removeUserFromProjectAndRepo(Long projectId, Long userId, CollaboratorDTO collaboratorDTO);

    boolean existsByIdIsDeleted(Long id);

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

    ResponseEntity<Object> addRepositoryToProject(Long projectId, Long repoId);


    List<ProjectDTO> getProjectsWithoutFigmaURL();

    List<ProjectDTO> getProjectsWithoutGoogleDriveLink();


    ProjectDTO getProjectDetailsById(Long projectId);

    ProjectDTO mapProjectToProjectDTO(Project project);

    Project mapProjectDTOToProject(ProjectDTO projectDTO);

}