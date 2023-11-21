package com.example.devopsproj.service.interfaces;

import com.example.devopsproj.dto.responsedto.FigmaDTO;
import com.example.devopsproj.dto.responsedto.FigmaScreenshotDTO;
import com.example.devopsproj.dto.responsedto.ProjectDTO;
import com.example.devopsproj.model.Figma;
import com.example.devopsproj.model.Project;

import java.util.List;
import java.util.Optional;
/**
 * Service interface for managing Figma-related operations, including creation,
 * retrieval, mapping, deletion, and association with users and screenshots.
 */
public interface FigmaService {

    Figma createFigma(FigmaDTO figmaDTO);

    List<Figma> getAllFigmaProjects();

    Optional<FigmaDTO> getFigmaById(Long figmaId);


    ProjectDTO mapProjectToProjectDTO(Project project);

    void softDeleteFigma(Long figmaId);

    String getFigmaURLByProjectId(Long projectId);


    void addUserAndScreenshots(Long figmaId, FigmaDTO figmaDTO);


    List<FigmaScreenshotDTO> getScreenshotsForFigmaId(Long figmaId);
    public List<FigmaDTO> getAllFigmaDTOs();

}
