package com.example.devopsproj.controller;

import com.example.devopsproj.dto.responsedto.FigmaDTO;
import com.example.devopsproj.dto.responsedto.FigmaScreenshotDTO;
import com.example.devopsproj.model.Figma;
import com.example.devopsproj.repository.FigmaRepository;
import com.example.devopsproj.service.interfaces.FigmaService;
import com.example.devopsproj.service.interfaces.JwtService;
import io.swagger.annotations.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/figmas")
@RequiredArgsConstructor
public class FigmaController {
    private final FigmaService figmaService;
    private final FigmaRepository figmaRepository;
    private final JwtService jwtService;
    private static final String INVALID_TOKEN = "Invalid Token";

    // Create a new Figma project.
    @PostMapping("/create")
    public ResponseEntity<String> createFigma(@RequestBody FigmaDTO figmaDTO) {
        figmaService.createFigma(figmaDTO);
        return ResponseEntity.ok("Figma created successfully");
    }

    // Get all Figma projects.
    @GetMapping("/getAll")
    public ResponseEntity<List<FigmaDTO>> getAllFigmaProjects() {
        List<FigmaDTO> figmaDTOs = figmaService.getAllFigmaDTOs();
        return ResponseEntity.ok(figmaDTOs);
    }

    @DeleteMapping("/{figmaId}")
    public ResponseEntity<String> deleteFigma(@PathVariable Long figmaId) {
        figmaService.deleteFigma(figmaId);
        return ResponseEntity.ok("Figma deleted successfully");
    }

    @GetMapping("/project/{projectId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getFigmaByProjectId(@PathVariable Long projectId) {
        String figmaURL = figmaService.getFigmaURLByProjectId(projectId);
        return ResponseEntity.ok(figmaURL);
    }

    // Get a specific Figma project by its ID.
    @GetMapping("/figma/{figmaId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getFigma(@PathVariable Long figmaId) {
        // Retrieve the Figma project by its ID.
        Optional<FigmaDTO> optionalFigmaDTO = figmaService.getFigmaById(figmaId);
        return ResponseEntity.of(Optional.ofNullable(optionalFigmaDTO));
    }

    // Add a user and screenshots to a Figma project.
    @PostMapping("/{figmaId}/user")
    public ResponseEntity<String> addUserAndScreenshotsToFigma(@PathVariable("figmaId") Long figmaId,
                                                               @RequestBody FigmaDTO figmaDTO) {
        figmaService.addUserAndScreenshots(figmaId, figmaDTO);
        return ResponseEntity.ok("User and screenshot added");
    }

    // Get screenshots for a specific Figma project by its ID.
    @GetMapping("/{figmaId}/screenshots")
    public ResponseEntity<List<FigmaScreenshotDTO>> getScreenshotsForFigmaId(@PathVariable("figmaId") Long figmaId) {
        List<FigmaScreenshotDTO> screenshotDTOList = figmaService.getScreenshotsForFigmaId(figmaId);
        return ResponseEntity.ok(screenshotDTOList);
    }
}
