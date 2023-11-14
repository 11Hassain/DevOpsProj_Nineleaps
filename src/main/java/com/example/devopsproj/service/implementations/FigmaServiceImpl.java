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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
public class FigmaServiceImpl implements FigmaService {

    @Autowired
    public FigmaServiceImpl(FigmaRepository figmaRepository, ProjectRepository projectRepository) {
        this.figmaRepository = figmaRepository;
        this.projectRepository = projectRepository;
    }

    private final FigmaRepository figmaRepository;
    private final ProjectRepository projectRepository;
    private static final Logger logger = LoggerFactory.getLogger(FigmaServiceImpl.class);

    /**
     * Create Figma
     *
     * @param figmaDTO The DTO used for the Figma.
     */
    @Override
    public void createFigma(FigmaDTO figmaDTO) {
        logger.info("Creating Figma project for project: {}", figmaDTO.getProjectDTO().getProjectName());

        Figma figma = new Figma();
        figma.setProject(DTOModelMapper.mapProjectDTOToProject(figmaDTO.getProjectDTO()));
        figma.setFigmaURL(figmaDTO.getFigmaURL());
        figmaRepository.save(figma);

        logger.info("Figma project created successfully.");
    }

    /**
     * Get all figma projects
     *
     * @return List of figma
     */
    @Override
    public List<Figma> getAllFigmaProjects() {
        logger.info("Fetching all Figma projects.");

        List<Project> activeProjects = projectRepository.findAllProjects();

        logger.info("Fetched {} Figma projects.", activeProjects.size());

        return activeProjects.stream()
                .map(Project::getFigma)
                .toList();
    }

    /**
     * Get figma by ID
     *
     * @param figmaId The ID of a Figma.
     * @return Figma DTO if the Figma is found. Otherwise, empty DTO.
     */
    @Override
    public Optional<FigmaDTO> getFigmaById(Long figmaId) {
        logger.info("Fetching Figma project by ID: {}", figmaId);

        Optional<Figma> optionalFigma = figmaRepository.findById(figmaId);
        return optionalFigma.map(figma -> new FigmaDTO(DTOModelMapper.mapProjectToProjectDTO(figma.getProject()), figma.getFigmaURL()));
    }

    /**
     * Delete Figma
     *
     * @param figmaId The ID of Figma.
     */
    @Override
    public void deleteFigma(Long figmaId) {
        logger.info("Deleting Figma project with ID: {}", figmaId);

        figmaRepository.deleteById(figmaId);

        logger.info("Figma project deleted successfully. ID: {}", figmaId);
    }

    /**
     * Save user and screenshots to Figma
     *
     * @param figmaId The ID of Figma for assigning user and saving screenshot.
     * @param figmaDTO The Dto used for the Figma.
     * @return Response with a string whether the info is saved or not.
     */
    @Override
    public String saveUserAndScreenshotsToFigma(Long figmaId, FigmaDTO figmaDTO){
        logger.info("Saving user and screenshots to Figma project with ID: {}", figmaId);

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

            logger.info("User and screenshot added successfully to Figma project. User: {}, Figma ID: {}", user, figmaId);

            return "User and screenshot added";
        } else {
            logger.warn("Figma project with ID {} not found.", figmaId);
            throw new NotFoundException("Figma not found");
        }
    }

    /**
     * Get Figma URL using the Project ID
     *
     * @param projectId The project ID linked with the Figma.
     * @return Response with a string whether success or failure.
     */
    @Override
    public String getFigmaURLByProjectId(Long projectId){
        logger.info("Fetching Figma URL for Project ID: {}", projectId);

        Optional<Figma> optionalFigma = figmaRepository.findFigmaByProjectId(projectId);
        if (optionalFigma.isPresent()) {
            Figma figma = optionalFigma.get();

            logger.info("Fetched Figma URL successfully. Project ID: {}, Figma URL: {}", projectId, figma.getFigmaURL());

            return figma.getFigmaURL();
        } else {
            logger.warn("Figma URL not found for Project ID: {}", projectId);
            throw new NotFoundException("Figma URL not found");
        }
    }

    /**
     * Get screenshot using Figma ID
     *
     * @param figmaId The ID of the Figma whose screenshot is fetched.
     * @return List of Figma using the Dto.
     */
    @Override
    public List<FigmaScreenshotDTO> getScreenshotsByFigmaId(Long figmaId){
        logger.info("Fetching screenshots for Figma project with ID: {}", figmaId);

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

                logger.info("Fetched {} screenshots for Figma project with ID: {}", screenshotDTOList.size(), figmaId);

                return screenshotDTOList;
            } else {
                logger.warn("No screenshots found for Figma project with ID: {}", figmaId);
                throw new NotFoundException("Screenshot Not found");
            }
        } else {
            logger.warn("Figma project with ID {} not found.", figmaId);
            throw new NotFoundException("Not Found");
        }
    }

}
