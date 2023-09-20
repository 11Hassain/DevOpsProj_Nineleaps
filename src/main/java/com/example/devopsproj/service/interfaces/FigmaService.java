package com.example.devopsproj.service.interfaces;

import com.example.devopsproj.dto.responseDto.FigmaDTO;
import com.example.devopsproj.dto.responseDto.ProjectDTO;
import com.example.devopsproj.model.Figma;
import com.example.devopsproj.model.Project;

import java.util.List;
import java.util.Optional;

public interface FigmaService {

    Figma createFigma(FigmaDTO figmaDTO);

    List<Figma> getAllFigmaProjects();

    Optional<FigmaDTO> getFigmaById(Long figmaId);

    void deleteFigma(Long figmaId);

    ProjectDTO mapProjectToProjectDTO(Project project);

}
