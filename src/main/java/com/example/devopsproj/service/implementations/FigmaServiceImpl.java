package com.example.devopsproj.service.implementations;

import com.example.devopsproj.dto.responsedto.FigmaDTO;
import com.example.devopsproj.dto.responsedto.FigmaScreenshotDTO;
import com.example.devopsproj.dto.responsedto.ProjectDTO;
import com.example.devopsproj.exceptions.FigmaCreationException;
import com.example.devopsproj.exceptions.FigmaNotFoundException;


import com.example.devopsproj.model.Figma;
import com.example.devopsproj.model.Project;
import com.example.devopsproj.repository.FigmaRepository;
import com.example.devopsproj.repository.ProjectRepository;
import com.example.devopsproj.service.interfaces.FigmaService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.*;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Service implementation for managing Figma projects.
 */
@Service
@RequiredArgsConstructor
public class FigmaServiceImpl implements FigmaService {

    private final FigmaRepository figmaRepository;
    private final ProjectRepository projectRepository;
        private static final Logger logger = LoggerFactory.getLogger(FigmaServiceImpl.class);
    /**
     * Creates a new Figma project.
     *
     * @param figmaDTO the DTO containing information about the Figma project.
     * @return the Figma entity representing the newly created Figma project.
     * @throws FigmaCreationException if the Figma creation fails due to data integrity violation or other errors.
     */
    @Override
    public Figma createFigma(FigmaDTO figmaDTO) {
        // Create a new Figma object and populate it with data from the DTO
        Figma figma = new Figma();
        figma.setProject(mapProjectDTOToProject(figmaDTO.getProjectDTO()));
        figma.setFigmaURL(figmaDTO.getFigmaURL());
        // Attempt to save the new Figma project
        Figma createdFigma = null;
        try {
            createdFigma = figmaRepository.save(figma);
            logger.info("Created Figma project with ID: {}", createdFigma.getFigmaId());
        } catch (DataIntegrityViolationException e) {
            throw new FigmaCreationException("Figma creation failed due to data integrity violation", e);
        } catch (Exception e) {
            throw new FigmaCreationException("Figma creation failed due to an error", e);
        }
        return createdFigma;
    }


    /**
     * Retrieves all Figma projects associated with active projects.
     *
     * @return a list of Figma entities representing all Figma projects.
     */
    @Override
    public List<Figma> getAllFigmaProjects() {
        logger.info("Retrieving all Figma projects");

        // Retrieve all active projects, map them to their associated Figma projects, and return as a list
        List<Figma> figmaProjects = projectRepository.findAllProjects().stream()
                .map(Project::getFigma)
                .toList(); // Use Stream.toList() for simplicity

        logger.info("Retrieved {} Figma projects", figmaProjects.size());

        return figmaProjects;
    }


    /**
     * Retrieves a Figma project by ID.
     *
     * @param figmaId the ID of the Figma project.
     * @return an Optional containing the DTO representing the retrieved Figma project, or an empty Optional if not found.
     */
    @Override
    public Optional<FigmaDTO> getFigmaById(Long figmaId) {
        logger.info("Retrieving Figma project by ID: {}", figmaId);

        // Retrieve the Figma project by ID
        Optional<Figma> optionalFigma = figmaRepository.findById(figmaId);

        if (optionalFigma.isPresent()) {
            Figma figma = optionalFigma.get();
            FigmaDTO figmaDTO = new FigmaDTO(mapProjectToProjectDTO(figma.getProject()), figma.getFigmaURL());

            logger.info("Retrieved Figma project by ID: {}", figmaId);

            return Optional.of(figmaDTO);
        } else {
            logger.info("Figma project not found for ID: {}", figmaId);
            return Optional.empty(); // Figma not found, returning empty Optional.
        }
    }

    /**
     * Soft deletes a Figma project by setting the 'deleted' flag to true.
     *
     * @param figmaId the ID of the Figma project to soft delete.
     * @throws FigmaNotFoundException if the Figma project with the given ID is not found.
     */
    @Override
    public void softDeleteFigma(Long figmaId) {
        logger.info("Soft deleting Figma with ID: {}", figmaId);

        Optional<Figma> figmaOptional = figmaRepository.findById(figmaId);
        if (figmaOptional.isPresent()) {
            Figma figma = figmaOptional.get();
            figma.setDeleted(true);
            figmaRepository.save(figma);

            logger.info("Figma with ID {} has been soft deleted", figmaId);
        } else {
            logger.error("Figma with ID {} not found, unable to soft delete", figmaId);
            throw new FigmaNotFoundException("Figma with ID " + figmaId + " not found");
        }
    }

    /**
     * Retrieves the Figma URL for a project by its ID.
     *
     * @param projectId the ID of the project.
     * @return the Figma URL associated with the project, or null if not found.
     */
    @Override
    public String getFigmaURLByProjectId(Long projectId) {
        logger.info("Retrieving Figma URL for project with ID: {}", projectId);

        Optional<Figma> optionalFigma = figmaRepository.findFigmaByProjectId(projectId);
        if (optionalFigma.isPresent()) {
            Figma figma = optionalFigma.get();
            String figmaURL = figma.getFigmaURL();

            logger.info("Retrieved Figma URL for project with ID {}: {}", projectId, figmaURL);

            return figmaURL;
        } else {
            logger.warn("Figma not found for project with ID: {}", projectId);
            return null;
        }
    }

    /**
     * Adds user and screenshots to a Figma project.
     *
     * @param figmaId   the ID of the Figma project.
     * @param figmaDTO  the DTO containing user and screenshot information.
     * @throws FigmaNotFoundException if the Figma project with the given ID is not found.
     */
    @Override
    public void addUserAndScreenshots(Long figmaId, FigmaDTO figmaDTO) {
        logger.info("Adding user and screenshots to Figma with ID: {}", figmaId);

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

            logger.info("Added user '{}' with screenshot image to Figma with ID: {}", user, figmaId);
        } else {
            logger.warn("Figma not found for ID: {}", figmaId);
            throw new FigmaNotFoundException("Figma not found for ID: " + figmaId);
        }
    }


    /**
     * Retrieves screenshots for a Figma project by its ID.
     *
     * @param figmaId the ID of the Figma project.
     * @return a list of DTOs representing the retrieved screenshots, or an empty list if not found.
     */
    @Override
    public List<FigmaScreenshotDTO> getScreenshotsForFigmaId(Long figmaId) {
        logger.info("Retrieving screenshots for Figma with ID: {}", figmaId);

        Optional<Figma> optionalFigma = figmaRepository.findById(figmaId);
        if (optionalFigma.isPresent()) {
            Figma figma = optionalFigma.get();
            Map<String, String> screenshotImagesByUser = figma.getScreenshotImagesByUser();

            if (screenshotImagesByUser != null && !screenshotImagesByUser.isEmpty()) {
                List<FigmaScreenshotDTO> screenshotDTOList = mapScreenshotImagesToDTOList(screenshotImagesByUser, figmaId);
                logger.info("Retrieved {} screenshots for Figma with ID: {}", screenshotDTOList.size(), figmaId);
                return screenshotDTOList;
            }
        }

        logger.info("No screenshots found for Figma with ID: {}", figmaId);
        return Collections.emptyList();
    }

    private List<FigmaScreenshotDTO> mapScreenshotImagesToDTOList(Map<String, String> screenshotImages, Long figmaId) {
        List<FigmaScreenshotDTO> screenshotDTOList = new ArrayList<>();
        for (Map.Entry<String, String> entry : screenshotImages.entrySet()) {
            FigmaScreenshotDTO screenshotDTO = mapScreenshotImageToDTO(entry.getKey(), entry.getValue());
            screenshotDTOList.add(screenshotDTO);
        }
        return screenshotDTOList;
    }


    private FigmaScreenshotDTO mapScreenshotImageToDTO(String user, String screenshotImageURL) {
        FigmaScreenshotDTO screenshotDTO = new FigmaScreenshotDTO();
        screenshotDTO.setUser(user);
        screenshotDTO.setScreenshotImageURL(screenshotImageURL);
        return screenshotDTO;
    }

    /**
     * Retrieves all Figma DTOs for active projects.
     *
     * @return a list of FigmaDTOs representing Figma projects associated with active projects.
     */
    @Override
    public List<FigmaDTO> getAllFigmaDTOs() {
        logger.info("Retrieving all Figma DTOs");

        List<Project> activeProjects = projectRepository.findAllProjects();
        List<FigmaDTO> figmaDTOs = new ArrayList<>();

        for (Project project : activeProjects) {
            Figma figma = project.getFigma();
            if (figma != null) {
                FigmaDTO figmaDTO = mapFigmaToFigmaDTO(figma);
                figmaDTOs.add(figmaDTO);
            }
        }

        logger.info("Retrieved {} Figma DTOs", figmaDTOs.size());
        return figmaDTOs;
    }

    private FigmaDTO mapFigmaToFigmaDTO(Figma figma) {
        Long figmaId = figma.getFigmaId();
        ProjectDTO projectDTO = mapProjectToProjectDTO(figma.getProject());
        String figmaURL = figma.getFigmaURL();

        return new FigmaDTO(figmaId, projectDTO, figmaURL);
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