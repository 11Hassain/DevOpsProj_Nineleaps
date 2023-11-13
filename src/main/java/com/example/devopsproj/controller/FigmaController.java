package com.example.devopsproj.controller;

import com.example.devopsproj.exceptions.NotFoundException;
import com.example.devopsproj.model.Figma;
import com.example.devopsproj.dto.responsedto.FigmaDTO;
import com.example.devopsproj.dto.responsedto.FigmaScreenshotDTO;
import com.example.devopsproj.service.interfaces.FigmaService;
import com.example.devopsproj.utils.DTOModelMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.*;

/**
 * The FigmaController class is responsible for handling RESTful API endpoints related to Figma projects and their associated functionalities.
 * It provides endpoints for creating, retrieving, updating, and deleting Figma projects, as well as adding users and screenshots to a Figma project.
 * .
 * This controller integrates with the FigmaService and JwtServiceImpl to perform Figma-related operations and authentication.
 *
 * @version 2.0
 */

@RestController
@RequestMapping("/api/v1/figmas")
@Validated
@RequiredArgsConstructor
public class FigmaController {

    private final FigmaService figmaService;
    private static final Logger logger = LoggerFactory.getLogger(FigmaController.class);

    /**
     * Create a Figma.
     *
     * @param figmaDTO The FigmaDTO containing the Figma data to be created.
     * @return ResponseEntity indicating the result of Figma creation.
     */
    @PostMapping("/create")
    @Operation(
            description = "Create Figma",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Figma created successfully"),
                    @ApiResponse(responseCode = "409", description = "Conflict"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> createFigma(@Valid @RequestBody FigmaDTO figmaDTO) {
        logger.info("Received a request to create a Figma.");

        try {
            figmaService.createFigma(figmaDTO);
            logger.info("Figma created successfully.");
            return ResponseEntity.ok("Figma created successfully");
        } catch (DataIntegrityViolationException e) {
            logger.error("Could not create Figma due to a conflict.");
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Could not create Figma");
        }
    }

    /**
     * Get all Figma projects.
     *
     * @return ResponseEntity with the list of Figma projects retrieved as FigmaDTOs.
     */
    @GetMapping("/getAll")
    @Operation(
            description = "Get All Figma Projects",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Figma projects retrieved successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getAllFigmaProjects() {
        logger.info("Received a request to retrieve all Figma projects.");

        List<Figma> figmaProjects = figmaService.getAllFigmaProjects();

        List<FigmaDTO> figmaDTOs = figmaProjects.stream()
                .filter(Objects::nonNull) // Filter out null values
                .map(figma -> {
                    if (figma.getProject() != null) {
                        return new FigmaDTO(
                                figma.getFigmaId(),
                                DTOModelMapper.mapProjectToProjectDTO(figma.getProject()),
                                figma.getFigmaURL());
                    } else {
                        return new FigmaDTO(
                                figma.getFigmaId(),
                                null, // Handle the case where Project is null
                                figma.getFigmaURL());
                    }
                })
                .toList();

        logger.info("Figma projects retrieved successfully.");
        return ResponseEntity.ok(figmaDTOs);
    }

    /**
     * Get a Figma by its ID.
     *
     * @param figmaId The ID of the Figma to retrieve.
     * @return ResponseEntity with the retrieved Figma or a not found status if not found.
     */
    @GetMapping("/get/{figmaId}")
    @Operation(
            description = "Get Figma by ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Figma retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "Figma not found"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getFigma(@PathVariable Long figmaId) {
        logger.info("Received a request to retrieve Figma by ID: {}", figmaId);

        Optional<FigmaDTO> optionalFigmaDTO = figmaService.getFigmaById(figmaId);

        if (optionalFigmaDTO.isPresent()) {
            logger.info("Figma retrieved successfully for ID: {}", figmaId);
            return ResponseEntity.ok(optionalFigmaDTO);
        } else {
            logger.info("Figma is not found for ID: {}", figmaId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Add a user and screenshots to a Figma by its ID.
     *
     * @param figmaId  The ID of the Figma to which to add a user and screenshots.
     * @param figmaDTO The FigmaDTO containing the user and screenshots data.
     * @return ResponseEntity indicating the result of the operation.
     */
    @PostMapping("/{figmaId}/user")
    @Operation(
            description = "Add User and Screenshots to Figma",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User and screenshot added"),
                    @ApiResponse(responseCode = "404", description = "Figma not found"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> addUserAndScreenshotsToFigma(@PathVariable("figmaId") Long figmaId,
                                                               @Valid @RequestBody FigmaDTO figmaDTO) {
        logger.info("Received a request to add a user and screenshots to Figma with ID: {}", figmaId);

        try {
            String result = figmaService.saveUserAndScreenshotsToFigma(figmaId, figmaDTO);
            logger.info("User and screenshots added to Figma with ID: {}", figmaId);
            return ResponseEntity.ok(result);
        } catch (NotFoundException e) {
            logger.info("Figma not found for ID: {}", figmaId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (RuntimeException e) {
            logger.error("Internal server error while adding user and screenshots to Figma with ID: {}", figmaId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Delete a Figma by its ID.
     *
     * @param figmaId The ID of the Figma to be deleted.
     * @return ResponseEntity indicating that the Figma has been deleted successfully.
     */
    @DeleteMapping("/{figmaId}")
    @Operation(
            description = "Delete Figma by ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Figma deleted successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> deleteFigma(@PathVariable Long figmaId) {
        logger.info("Received a request to delete Figma by ID: {}", figmaId);

        figmaService.deleteFigma(figmaId);

        logger.info("Figma deleted successfully for ID: {}", figmaId);
        return ResponseEntity.ok("Figma deleted successfully");
    }

    /**
     * Get a Figma URL by the associated Project ID.
     *
     * @param projectId The ID of the Project for which to retrieve the Figma URL.
     * @return ResponseEntity with the retrieved Figma URL or a not found status if not found.
     */
    @GetMapping("/project/{projectId}")
    @Operation(
            description = "Get Figma by Project ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Figma URL retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "Figma not found"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getFigmaByProjectId(@PathVariable Long projectId) {
        logger.info("Received a request to retrieve Figma URL by Project ID: {}", projectId);

        try {
            String figmaURL = figmaService.getFigmaURLByProjectId(projectId);
            logger.info("Figma URL retrieved successfully for Project ID: {}", projectId);
            return ResponseEntity.ok(figmaURL);
        } catch (NotFoundException e) {
            logger.info("Figma not found for Project ID: {}", projectId);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Get screenshots for a Figma by its ID.
     *
     * @param figmaId The ID of the Figma for which to retrieve screenshots.
     * @return ResponseEntity with the retrieved screenshots or appropriate error responses.
     */
    @GetMapping("/{figmaId}/screenshots")
    @Operation(
            description = "Get Screenshots for Figma by ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Screenshots retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "Figma not found"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getScreenshotsForFigmaId(@PathVariable("figmaId") Long figmaId) {
        logger.info("Received a request to retrieve screenshots for Figma with ID: {}", figmaId);

        try {
            List<FigmaScreenshotDTO> figmaScreenshotDTOS = figmaService.getScreenshotsByFigmaId(figmaId);
            logger.info("Screenshots retrieved successfully for Figma with ID: {}", figmaId);
            return ResponseEntity.ok(figmaScreenshotDTOS);
        } catch (NotFoundException e) {
            logger.info("Figma is not found for ID: {}", figmaId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            logger.error("Internal server error while retrieving screenshots for Figma with ID: {}", figmaId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
