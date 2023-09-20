package com.example.devopsproj.service.implementations;

//package com.example.devopsproj.service;

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

//    @Override
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
}

