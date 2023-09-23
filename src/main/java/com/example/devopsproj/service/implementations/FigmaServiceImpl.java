package com.example.devopsproj.service.implementations;

import com.example.devopsproj.dto.responseDto.ProjectDTO;
import com.example.devopsproj.model.Figma;
import com.example.devopsproj.dto.responseDto.FigmaDTO;
import com.example.devopsproj.model.Project;
import com.example.devopsproj.repository.FigmaRepository;
import com.example.devopsproj.repository.ProjectRepository;
import com.example.devopsproj.service.interfaces.FigmaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FigmaServiceImpl implements FigmaService {
    @Autowired
    private FigmaRepository figmaRepository;
    @Autowired ProjectRepository projectRepository;

    @Override
    public Figma createFigma(FigmaDTO figmaDTO) {
        Figma figma = new Figma();
        figma.setProject(mapProjectDTOToProject(figmaDTO.getProjectDTO()));
        figma.setFigmaURL(figmaDTO.getFigmaURL());
        return figmaRepository.save(figma);
    }

    @Override
    public List<Figma> getAllFigmaProjects() {
        List<Project> activeProjects = projectRepository.findAllProjects();
        List<Figma> figmaProjects = activeProjects.stream()
                .map(Project::getFigma)
                .collect(Collectors.toList());
        return figmaProjects;
    }

    @Override
    public Optional<FigmaDTO> getFigmaById(Long figmaId) {
        Optional<Figma> optionalFigma = figmaRepository.findById(figmaId);
        return optionalFigma.map(figma -> new FigmaDTO(mapProjectToProjectDTO(figma.getProject()), figma.getFigmaURL()));
    }

    @Override
    public void deleteFigma(Long figmaId) {
        figmaRepository.deleteById(figmaId);
    }

    public ProjectDTO mapProjectToProjectDTO(Project project) {
        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setProjectId(project.getProjectId());
        projectDTO.setProjectName(project.getProjectName());
        return projectDTO;
    }

    public Project mapProjectDTOToProject(ProjectDTO projectDTO) {
        Project project = new Project();
        project.setProjectId(projectDTO.getProjectId());
        project.setProjectName(projectDTO.getProjectName());
        return project;
    }
}
