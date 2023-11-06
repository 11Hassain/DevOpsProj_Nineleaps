package com.example.devopsproj.controller;

import com.example.devopsproj.constants.CommonConstants;
import com.example.devopsproj.constants.FigmaConstants;
import com.example.devopsproj.dto.responsedto.FigmaDTO;
import com.example.devopsproj.dto.responsedto.FigmaScreenshotDTO;
import com.example.devopsproj.service.interfaces.FigmaService;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.*;


@RestController
@RequestMapping("/api/v1/figmas")
public class FigmaController {
    private final FigmaService figmaService;

    public FigmaController(FigmaService figmaService) {
        this.figmaService = figmaService;
    }


    // Create a new Figma project.
    @PostMapping("/create")
    @ApiOperation("Create a Figma project")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<String> createFigma(@RequestBody FigmaDTO figmaDTO) {
        figmaService.createFigma(figmaDTO);
        return ResponseEntity.ok(CommonConstants.CREATED_SUCCESSFULLY);
    }

    // Get all Figma projects.
    @GetMapping("/getAll")
    @ApiOperation("Get all Figma projects")
    @ResponseStatus(HttpStatus.OK) // Replace with the appropriate status code
    public ResponseEntity<List<FigmaDTO>> getAllFigmaProjects() {
        List<FigmaDTO> figmaDTOs = figmaService.getAllFigmaDTOs();
        return ResponseEntity.ok(figmaDTOs);
    }

    @DeleteMapping("/{figmaId}")
    @ApiOperation("Soft delete a Figma project")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> deleteFigma(@PathVariable Long figmaId) {
        figmaService.softDeleteFigma(figmaId);
        return ResponseEntity.ok(CommonConstants.DELETED_SUCCESSFULLY);
    }



    @GetMapping("/project/{projectId}")
    @ApiOperation("Get Figma by project ID")
    @ResponseStatus(HttpStatus.OK) // Replace with the appropriate status code
    public ResponseEntity<Object> getFigmaByProjectId(@PathVariable Long projectId) {
        String figmaURL = figmaService.getFigmaURLByProjectId(projectId);
        return ResponseEntity.ok(figmaURL);
    }



    // Get a specific Figma project by its ID.
    @GetMapping("/figma/{figmaId}")
    @ApiOperation("Get a specific Figma project by ID")
    @ResponseStatus(HttpStatus.OK) // Replace with the appropriate status code
    public ResponseEntity<Object> getFigma(@PathVariable Long figmaId) {
        Optional<FigmaDTO> optionalFigmaDTO = figmaService.getFigmaById(figmaId);
        return ResponseEntity.of(Optional.ofNullable(optionalFigmaDTO));
    }


    // Add a user and screenshots to a Figma project.
    @PostMapping("/{figmaId}/user")
    @ApiOperation("Add a user and screenshots to a Figma project")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<String> addUserAndScreenshotsToFigma(@PathVariable("figmaId") Long figmaId,
                                                               @RequestBody FigmaDTO figmaDTO) {
        figmaService.addUserAndScreenshots(figmaId, figmaDTO);
        return ResponseEntity.ok(FigmaConstants.USER_SCREENSHOTS_ADDED);
    }


    // Get screenshots for a specific Figma project by its ID.
    @GetMapping("/{figmaId}/screenshots")
    @ApiOperation("Get screenshots for a specific Figma project by ID")
    @ResponseStatus(HttpStatus.OK) // Replace with the appropriate status code
    public ResponseEntity<List<FigmaScreenshotDTO>> getScreenshotsForFigmaId(@PathVariable("figmaId") Long figmaId) {
        List<FigmaScreenshotDTO> screenshotDTOList = figmaService.getScreenshotsForFigmaId(figmaId);
        return ResponseEntity.ok(screenshotDTOList);
    }

}
