package com.example.DevOpsProj.service;

import com.example.DevOpsProj.dto.responseDto.FigmaDTO;
import com.example.DevOpsProj.dto.responseDto.ProjectDTO;
import com.example.DevOpsProj.model.Figma;
import com.example.DevOpsProj.model.Project;
import com.example.DevOpsProj.repository.FigmaRepository;
import com.example.DevOpsProj.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FigmaService {
    @Autowired
    private FigmaRepository figmaRepository;
    @Autowired ProjectRepository projectRepository;

    public Figma createFigma(FigmaDTO figmaDTO) {
        Figma figma = new Figma();
        figma.setProject(mapProjectDTOToProject(figmaDTO.getProjectDTO()));
        figma.setFigmaURL(figmaDTO.getFigmaURL());
        return figmaRepository.save(figma);
    }

    public List<Figma> getAllFigmaProjects() {
        List<Project> activeProjects = projectRepository.findAllProjects();
        List<Figma> figmaProjects = activeProjects.stream()
                .map(Project::getFigma)
                .collect(Collectors.toList());
        return figmaProjects;
    }

    public Optional<FigmaDTO> getFigmaById(Long figmaId) {
        Optional<Figma> optionalFigma = figmaRepository.findById(figmaId);
        return optionalFigma.map(figma -> new FigmaDTO(mapProjectToProjectDTO(figma.getProject()), figma.getFigmaURL()));
    }

    public ProjectDTO mapProjectToProjectDTO(Project project) {
        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setProjectId(project.getProjectId());
        projectDTO.setProjectName(project.getProjectName());
        return projectDTO;
    }
    public FigmaDTO mapFigmaToFigmatDTO(Figma figma) {
        FigmaDTO figmaDTO = new FigmaDTO();
        figmaDTO.setFigmaId(figma.getFigmaId());
        figmaDTO.setFigmaURL(figma.getFigmaURL());
        return figmaDTO;
    }

    public Project mapProjectDTOToProject(ProjectDTO projectDTO) {
        Project project = new Project();
        project.setProjectId(projectDTO.getProjectId());
        project.setProjectName(projectDTO.getProjectName());
        return project;
    }
    public Figma mapFigmaDTOToFigma(FigmaDTO figmaDTO) {
        Figma figma= new Figma();
        figma.setFigmaId(figmaDTO.getFigmaId());
        figma.setFigmaURL(figmaDTO.getFigmaURL());
        return figma;
    }

    public void deleteFigma(Long figmaId) {
        figmaRepository.deleteById(figmaId);
    }

}
