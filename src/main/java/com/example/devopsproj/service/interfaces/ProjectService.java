package com.example.devopsproj.service.interfaces;

import com.example.devopsproj.commons.enumerations.EnumRole;
import com.example.devopsproj.dto.responsedto.*;
import com.example.devopsproj.model.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface ProjectService {
    Project saveProject(ProjectDTO projectDTO);

    ProjectDTO createProject(ProjectDTO projectDTO);

    Optional<Project> getProjectById(Long id);

    List<Project> getAll();

    List<Project> getAllProjects();

    Project updateProject(Project updatedProject);

    List<User> getAllUsersByProjectId(Long projectId); // Add this method

    List<User> getAllUsersByProjectIdAndRole(Long projectId, EnumRole role);

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

    List<User> getUsersByProjectIdAndRole(Long projectId, EnumRole role);

    List<ProjectDTO> getProjectsWithoutFigmaURL();

    List<ProjectDTO> getProjectsWithoutGoogleDriveLink();

    ProjectDTO getProjectDetailsById(Long projectId);

    ProjectDTO mapProjectToProjectDTO(Project project);
}
