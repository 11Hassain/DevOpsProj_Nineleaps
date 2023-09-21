package com.example.devopsproj.service.implementations;

import com.example.devopsproj.dto.responseDto.FigmaDTO;
import com.example.devopsproj.dto.responseDto.ProjectDTO;
import com.example.devopsproj.model.Figma;
import com.example.devopsproj.model.Project;
import com.example.devopsproj.repository.FigmaRepository;
import com.example.devopsproj.repository.ProjectRepository;
import com.example.devopsproj.service.interfaces.FigmaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FigmaServiceImpl implements FigmaService {

    private final FigmaRepository figmaRepository;
    private final ProjectRepository projectRepository;

    // Create a new Figma project
    @Override
    public Figma createFigma(FigmaDTO figmaDTO) {
        // Create a new Figma object and populate it with data from the DTO
        Figma figma = new Figma();
        figma.setProject(mapProjectDTOToProject(figmaDTO.getProjectDTO()));
        figma.setFigmaURL(figmaDTO.getFigmaURL());

        // Save the new Figma project to the repository
        return figmaRepository.save(figma);
    }

    // Get all Figma projects
    @Override
    public List<Figma> getAllFigmaProjects() {
        // Retrieve all active projects
        List<Project> activeProjects = projectRepository.findAllProjects();

        // Map each active project to its associated Figma project and collect them in a list
        List<Figma> figmaProjects = activeProjects.stream()
                .map(Project::getFigma)
                .collect(Collectors.toList());

        return figmaProjects;
    }

    // Get a Figma project by its ID
    @Override
    public Optional<FigmaDTO> getFigmaById(Long figmaId) {
        // Retrieve the Figma project by ID
        Optional<Figma> optionalFigma = figmaRepository.findById(figmaId);

        // Map the Figma project to a FigmaDTO if it exists
        return optionalFigma.map(figma -> new FigmaDTO(mapProjectToProjectDTO(figma.getProject()), figma.getFigmaURL()));
    }

    // Delete a Figma project by its ID
    @Override
    public void deleteFigma(Long figmaId) {
        // Delete the Figma project by ID
        figmaRepository.deleteById(figmaId);
    }

    // Helper method to map Project to ProjectDTO
    public ProjectDTO mapProjectToProjectDTO(Project project) {
        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setProjectId(project.getProjectId());
        projectDTO.setProjectName(project.getProjectName());
        return projectDTO;
    }

    // Helper method to map Figma to FigmaDTO
    public FigmaDTO mapFigmaToFigmatDTO(Figma figma) {
        FigmaDTO figmaDTO = new FigmaDTO();
        figmaDTO.setFigmaId(figma.getFigmaId());
        figmaDTO.setFigmaURL(figma.getFigmaURL());
        return figmaDTO;
    }

    // Helper method to map ProjectDTO to Project
    public Project mapProjectDTOToProject(ProjectDTO projectDTO) {
        Project project = new Project();
        project.setProjectId(projectDTO.getProjectId());
        project.setProjectName(projectDTO.getProjectName());
        return project;
    }

    // Helper method to map FigmaDTO to Figma
    public Figma mapFigmaDTOToFigma(FigmaDTO figmaDTO) {
        Figma figma = new Figma();
        figma.setFigmaId(figmaDTO.getFigmaId());
        figma.setFigmaURL(figmaDTO.getFigmaURL());
        return figma;
    }
}