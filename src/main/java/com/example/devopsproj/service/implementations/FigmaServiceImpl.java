package com.example.devopsproj.service.implementations;

import com.example.devopsproj.dto.responsedto.FigmaDTO;
import com.example.devopsproj.dto.responsedto.FigmaScreenshotDTO;
import com.example.devopsproj.dto.responsedto.ProjectDTO;
import com.example.devopsproj.exceptions.FigmaCreationException;
import com.example.devopsproj.exceptions.FigmaNotFoundException;
import com.example.devopsproj.exceptions.FigmaServiceException;
import com.example.devopsproj.exceptions.UnauthorizedException;
import com.example.devopsproj.model.Figma;
import com.example.devopsproj.model.Project;
import com.example.devopsproj.repository.FigmaRepository;
import com.example.devopsproj.repository.ProjectRepository;
import com.example.devopsproj.service.interfaces.FigmaService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FigmaServiceImpl implements FigmaService {

    private final FigmaRepository figmaRepository;
    private final ProjectRepository projectRepository;


    // Create a new Figma project
    @Override
    public Figma createFigma(FigmaDTO figmaDTO) throws FigmaCreationException {
        // Create a new Figma object and populate it with data from the DTO
        Figma figma = new Figma();
        figma.setProject(mapProjectDTOToProject(figmaDTO.getProjectDTO()));
        figma.setFigmaURL(figmaDTO.getFigmaURL());

        try {
            // Save the new Figma project to the repository
            return figmaRepository.save(figma);
        } catch (DataIntegrityViolationException e) {
            // If there's a specific data integrity violation related to Figma creation,
            // you can catch it and rethrow it as your custom exception.
            throw new FigmaCreationException("Could not create Figma", e);
        } catch (Exception e) {
            // Handle any other exceptions that may occur and rethrow them as needed
            throw new FigmaCreationException("An error occurred while creating Figma", e);
        }
    }




    // Get all Figma projects
    @Override
    public List<Figma> getAllFigmaProjects() {
        try {
            // Retrieve all active projects, map them to their associated Figma projects, and return as a list
            return projectRepository.findAllProjects().stream()
                    .map(Project::getFigma)
                    .toList(); // Use Stream.toList() for simplicity
        } catch (Exception e) {
            throw new FigmaServiceException("An error occurred while retrieving Figma projects", e);
        }
    }




    @Override
    public Optional<FigmaDTO> getFigmaById(Long figmaId) {
        // Retrieve the Figma project by ID
        Optional<Figma> optionalFigma = figmaRepository.findById(figmaId);

        if (optionalFigma.isPresent()) {
            return optionalFigma.map(figma -> new FigmaDTO(mapProjectToProjectDTO(figma.getProject()), figma.getFigmaURL()));
        } else {
            return Optional.empty(); // Figma not found, returning empty Optional.
        }
    }

    @Override
    public void deleteFigma(Long figmaId) {
        try {
            // Delete the Figma project by ID
            figmaRepository.deleteById(figmaId);
        } catch (EmptyResultDataAccessException e) {
            throw new FigmaNotFoundException("Figma with ID " + figmaId + " not found", e);
        } catch (Exception e) {
            throw new FigmaServiceException("An error occurred while deleting Figma by ID", e);
        }
    }
    @Override
    public String getFigmaURLByProjectId(Long projectId) {
        Optional<Figma> optionalFigma = figmaRepository.findFigmaByProjectId(projectId);
        if (optionalFigma.isPresent()) {
            Figma figma = optionalFigma.get();
            return figma.getFigmaURL();
        } else {
            return null;
        }
    }

    @Override
    public void addUserAndScreenshots(Long figmaId, FigmaDTO figmaDTO) {
        Optional<Figma> optionalFigma = figmaRepository.findById(figmaId);
        if (optionalFigma.isPresent()) {
            Figma figma = optionalFigma.get();
            String user = figmaDTO.getUser();

            // Get or create the map of screenshot images by user.
            Map<String, String> screenshotImagesByUser = figma.getScreenshotImagesByUser();
            if (screenshotImagesByUser == null) {
                screenshotImagesByUser = new HashMap<>();
                figma.setScreenshotImagesByUser(screenshotImagesByUser); // Set the map back to the Figma object
            }

            // Add the screenshot image for the specified user.
            screenshotImagesByUser.put(user, figmaDTO.getScreenshotImage());

            figmaRepository.save(figma);
        } else {
            throw new FigmaNotFoundException("Figma not found for ID: " + figmaId);
        }
    }


    @Override
    public List<FigmaScreenshotDTO> getScreenshotsForFigmaId(Long figmaId) {
        Optional<Figma> optionalFigma = figmaRepository.findById(figmaId);
        if (optionalFigma.isPresent()) {
            Figma figma = optionalFigma.get();
            Map<String, String> screenshotImagesByUser = figma.getScreenshotImagesByUser();

            if (screenshotImagesByUser != null && !screenshotImagesByUser.isEmpty()) {
                List<FigmaScreenshotDTO> screenshotDTOList = new ArrayList<>();
                for (Map.Entry<String, String> entry : screenshotImagesByUser.entrySet()) {
                    FigmaScreenshotDTO screenshotDTO = new FigmaScreenshotDTO();
                    screenshotDTO.setUser(entry.getKey());
                    screenshotDTO.setScreenshotImageURL(entry.getValue());
                    screenshotDTOList.add(screenshotDTO);
                }
                return screenshotDTOList;
            }
        }
        return Collections.emptyList();
    }

    @Override
    public List<FigmaDTO> getAllFigmaDTOs() {
        List<Project> activeProjects = projectRepository.findAllProjects();
        List<FigmaDTO> figmaDTOs = new ArrayList<>();

        for (Project project : activeProjects) {
            Figma figma = project.getFigma();
            if (figma != null) {
                FigmaDTO figmaDTO = new FigmaDTO(
                        figma.getFigmaId(),
                        mapProjectToProjectDTO(figma.getProject()),
                        figma.getFigmaURL()
                );
                figmaDTOs.add(figmaDTO);
            }
        }

        return figmaDTOs;
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