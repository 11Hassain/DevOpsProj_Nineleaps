package com.example.devopsproj.utils;

import com.example.devopsproj.dto.responsedto.ProjectDTO;
import com.example.devopsproj.model.Project;

/**
 * The `DTOModelMapper` class provides utility methods for mapping between DTO (Data Transfer Object) and
 * model entities, specifically for Project and ProjectDTO mapping.
 *
 * @version 1.0
 */

public class DTOModelMapper {

    private DTOModelMapper() {
        // This constructor is intentionally left empty because the class
        // only contains static methods for mapping and doesn't need to be instantiated.
        throw new UnsupportedOperationException("This class should not be instantiated.");
    }

    public static ProjectDTO mapProjectToProjectDTO(Project project) {
        if (project != null) {
            ProjectDTO projectDTO = new ProjectDTO();
            projectDTO.setProjectId(project.getProjectId());
            projectDTO.setProjectName(project.getProjectName());
            return projectDTO;
        }
        return null;
    }

    public static Project mapProjectDTOToProject(ProjectDTO projectDTO) {
        if (projectDTO != null) {
            Project project = new Project();
            project.setProjectId(projectDTO.getProjectId());
            project.setProjectName(projectDTO.getProjectName());
            return project;
        }
        return null;
    }
}
