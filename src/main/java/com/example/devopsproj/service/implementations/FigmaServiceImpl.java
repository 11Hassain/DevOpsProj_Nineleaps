package com.example.devopsproj.service.implementations;

import com.example.devopsproj.model.Figma;
import com.example.devopsproj.dto.responsedto.FigmaDTO;
import com.example.devopsproj.model.Project;
import com.example.devopsproj.repository.FigmaRepository;
import com.example.devopsproj.repository.ProjectRepository;
import com.example.devopsproj.service.interfaces.FigmaService;
import com.example.devopsproj.utils.DTOModelMapper;
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
        figma.setProject(DTOModelMapper.mapProjectDTOToProject(figmaDTO.getProjectDTO()));
        figma.setFigmaURL(figmaDTO.getFigmaURL());
        return figmaRepository.save(figma);
    }

    @Override
    public List<Figma> getAllFigmaProjects() {
        List<Project> activeProjects = projectRepository.findAllProjects();
        return activeProjects.stream()
                .map(Project::getFigma)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<FigmaDTO> getFigmaById(Long figmaId) {
        Optional<Figma> optionalFigma = figmaRepository.findById(figmaId);
        return optionalFigma.map(figma -> new FigmaDTO(DTOModelMapper.mapProjectToProjectDTO(figma.getProject()), figma.getFigmaURL()));
    }

    @Override
    public void deleteFigma(Long figmaId) {
        figmaRepository.deleteById(figmaId);
    }


}
