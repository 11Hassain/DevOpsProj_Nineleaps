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

            try {
                figmaService.createFigma(figmaDTO);

                return ResponseEntity.ok("Figma created successfully");

            } catch (DataIntegrityViolationException e) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Could not create figma");
            }

    }

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

            return ResponseEntity.ok(figmaDTOs);

    }

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

            Optional<FigmaDTO> optionalFigmaDTO = figmaService.getFigmaById(figmaId);
            if (optionalFigmaDTO.isPresent()) {
                return ResponseEntity.ok(optionalFigmaDTO);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

    }

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
                                                               @Valid @RequestBody FigmaDTO figmaDTO)
    {

            try {
                String result = figmaService.saveUserAndScreenshotsToFigma(figmaId, figmaDTO);
                return ResponseEntity.ok(result);
            } catch (NotFoundException e) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            } catch (RuntimeException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
            }

    }


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

            figmaService.deleteFigma(figmaId);
            return ResponseEntity.ok("Figma deleted successfully");

    }

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

            try {
                String figmaURL = figmaService.getFigmaURLByProjectId(projectId);
                return ResponseEntity.ok(figmaURL);
            } catch (NotFoundException e){
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

    }

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

            try {
                List<FigmaScreenshotDTO> figmaScreenshotDTOS = figmaService.getScreenshotsByFigmaId(figmaId);
                return ResponseEntity.ok(figmaScreenshotDTOS);
            } catch (NotFoundException e) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }

    }
}
