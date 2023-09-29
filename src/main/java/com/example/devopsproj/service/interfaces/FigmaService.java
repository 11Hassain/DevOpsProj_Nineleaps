package com.example.devopsproj.service.interfaces;

import com.example.devopsproj.dto.responsedto.FigmaDTO;
import com.example.devopsproj.dto.responsedto.FigmaScreenshotDTO;
import com.example.devopsproj.model.Figma;

import java.util.List;
import java.util.Optional;

public interface FigmaService {
    void createFigma(FigmaDTO figmaDTO);

    List<Figma> getAllFigmaProjects();

    Optional<FigmaDTO> getFigmaById(Long figmaId);

    void deleteFigma(Long figmaId);

    String saveUserAndScreenshotsToFigma(Long figmaId, FigmaDTO figmaDTO);

    String getFigmaURLByProjectId(Long projectId);

    List<FigmaScreenshotDTO> getScreenshotsByFigmaId(Long figmaId);
}
