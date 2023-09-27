package com.example.devopsproj.service.interfaces;

import com.example.devopsproj.commons.enumerations.EnumRole;
import com.example.devopsproj.dto.responsedto.ProjectDTO;
import com.example.devopsproj.dto.responsedto.ProjectNamePeopleCountDTO;
import com.example.devopsproj.model.Project;
import com.example.devopsproj.model.User;

import java.util.List;
import java.util.Optional;

public interface ProjectService {
    //implementing DTO pattern for project for saving project
    void saveProject(ProjectDTO projectDTO);

    Optional<Project> getProjectById(Long id);

    List<Project> getAll();

    List<Project> getAllProjects();

    Project updateProject(Project updatedProject);

    //get all users in the project based on project id
    List<User> getAllUsersByProjectId(Long projectId);

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

    List<User> getUsersByProjectIdAndRole(Long projectId, EnumRole role);

    List<ProjectDTO> getProjectsWithoutFigmaURL();

    List<ProjectDTO> getProjectsWithoutGoogleDriveLink();

    ProjectDTO getProjectDetailsById(Long projectId);
}
