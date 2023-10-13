package com.example.devopsproj.service.implementations;

import com.example.devopsproj.dto.responsedto.FigmaScreenshotDTO;
import com.example.devopsproj.exceptions.NotFoundException;
import com.example.devopsproj.model.Figma;
import com.example.devopsproj.dto.responsedto.FigmaDTO;
import com.example.devopsproj.model.Project;
import com.example.devopsproj.repository.FigmaRepository;
import com.example.devopsproj.repository.ProjectRepository;
import com.example.devopsproj.service.interfaces.FigmaService;
import com.example.devopsproj.utils.DTOModelMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * The `FigmaServiceImpl` class provides services for managing Figma projects and their associated screenshots.
 * It includes methods for creating Figma projects, retrieving Figma project details, managing screenshots,
 * and performing various operations related to Figma projects.
 *
 * @version 2.0
 */

@Service
@RequiredArgsConstructor
public class FigmaServiceImpl implements FigmaService {

    private final FigmaRepository figmaRepository;
    private final ProjectRepository projectRepository;

    @Override
    public void createFigma(FigmaDTO figmaDTO) {
        Figma figma = new Figma();
        figma.setProject(DTOModelMapper.mapProjectDTOToProject(figmaDTO.getProjectDTO()));
        figma.setFigmaURL(figmaDTO.getFigmaURL());
        figmaRepository.save(figma);
    }

    @Override
    public List<Figma> getAllFigmaProjects() {
        List<Project> activeProjects = projectRepository.findAllProjects();
        return activeProjects.stream()
                .map(Project::getFigma)
                .toList();
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

    @Override
    public String saveUserAndScreenshotsToFigma(Long figmaId, FigmaDTO figmaDTO){
        Optional<Figma> optionalFigma = figmaRepository.findById(figmaId);
        if (optionalFigma.isPresent()) {
            Figma figma = optionalFigma.get();
            String user = figmaDTO.getUser();

            // Get or create the map of screenshot images by user
            Map<String, String> screenshotImagesByUser = figma.getScreenshotImagesByUser();
            if (screenshotImagesByUser == null) {
                screenshotImagesByUser = new HashMap<>();
            }

            // Add the screenshot image for the specified user
            screenshotImagesByUser.put(user, figmaDTO.getScreenshotImage());
            figma.setScreenshotImagesByUser(screenshotImagesByUser);

            figmaRepository.save(figma);
            return "User and screenshot added";
        } else {
            throw new NotFoundException("Figma not found");
        }
    }

    @Override
    public String getFigmaURLByProjectId(Long projectId){
        Optional<Figma> optionalFigma = figmaRepository.findFigmaByProjectId(projectId);
        if (optionalFigma.isPresent()) {
            Figma figma = optionalFigma.get();
            return figma.getFigmaURL();
        } else {
            throw new NotFoundException("Figma URL not found");
        }
    }

    @Override
    public List<FigmaScreenshotDTO> getScreenshotsByFigmaId(Long figmaId){
        Optional<Figma> optionalFigma = figmaRepository.findById(figmaId);
        if (optionalFigma.isPresent()) {
            Figma figma = optionalFigma.get();

            List<FigmaScreenshotDTO> screenshotDTOList = new ArrayList<>();
            Map<String, String> screenshotImagesByUser = figma.getScreenshotImagesByUser();

            if (screenshotImagesByUser != null && !screenshotImagesByUser.isEmpty()) {
                for (Map.Entry<String, String> entry : screenshotImagesByUser.entrySet()) {
                    FigmaScreenshotDTO screenshotDTO = new FigmaScreenshotDTO();
                    screenshotDTO.setUser(entry.getKey());
                    screenshotDTO.setScreenshotImageURL(entry.getValue());

                    screenshotDTOList.add(screenshotDTO);
                }
                return screenshotDTOList;
            } else {
                throw new NotFoundException("Screenshot Not found");
            }
        } else {
            throw new NotFoundException("Not Found");
        }
    }

}
