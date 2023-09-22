package com.example.devopsproj.controller;

import com.example.devopsproj.model.Figma;
import com.example.devopsproj.service.FigmaService;
import com.example.devopsproj.service.JwtService;
import com.example.devopsproj.dto.responseDto.FigmaDTO;
import com.example.devopsproj.dto.responseDto.FigmaScreenshotDTO;
import com.example.devopsproj.repository.FigmaRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/figmas")
@Validated
public class FigmaController {
    @Autowired
    private FigmaService figmaService;
    @Autowired
    private FigmaRepository figmaRepository;
    @Autowired
    private JwtService jwtService;
    private static final String INVALID_TOKEN = "Invalid Token";

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
    public ResponseEntity<String> createFigma(@RequestBody FigmaDTO figmaDTO,
                                              @RequestHeader("AccessToken") String accessToken) {
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            try {
                // Create a new Figma entity using the provided projectName and figmaURL
                Figma figma = figmaService.createFigma(figmaDTO);

                // Optionally, you can access the generated figmaId if needed
                Long figmaId = figma.getFigmaId();

                return ResponseEntity.ok("Figma created successfully");

            } catch (DataIntegrityViolationException e) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Could not create figma");
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
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
    public ResponseEntity<Object> getAllFigmaProjects(@RequestHeader("AccessToken") String accessToken) {
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            List<Figma> figmaProjects = figmaService.getAllFigmaProjects();

            List<FigmaDTO> figmaDTOs = figmaProjects.stream()
                    .filter(Objects::nonNull) // Filter out null values
                    .map(figma -> new FigmaDTO(
                            figma.getFigmaId(),
                            figmaService.mapProjectToProjectDTO(figma.getProject()),
                            figma.getFigmaURL()))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(figmaDTOs);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
        }
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
    public ResponseEntity<Object> getFigma(@PathVariable Long figmaId,
                                           @RequestHeader("AccessToken") String accessToken) {
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            Optional<FigmaDTO> optionalFigmaDTO = figmaService.getFigmaById(figmaId);
            if (optionalFigmaDTO.isPresent()) {
                return ResponseEntity.ok(optionalFigmaDTO);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
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
                                                               @RequestBody FigmaDTO figmaDTO,
                                                               @RequestHeader("AccessToken") String accessToken)
    {
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            try {
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
                    return ResponseEntity.ok("User and screenshot added");
                } else {
                    return ResponseEntity.notFound().build();
                }
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
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
    public ResponseEntity<Object> getFigmaByProjectId(@PathVariable Long projectId,
                                                      @RequestHeader("AccessToken") String accessToken) {
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            Optional<Figma> optionalFigma = figmaRepository.findFigmaByProjectId(projectId);
            if (optionalFigma.isPresent()) {
                Figma figma = optionalFigma.get();
                String figmaURL = figma.getFigmaURL();
                return ResponseEntity.ok(figmaURL);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
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
    public ResponseEntity<List<FigmaScreenshotDTO>> getScreenshotsForFigmaId(@PathVariable("figmaId") Long figmaId) {
        try {
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
                    return ResponseEntity.ok(screenshotDTOList);
                } else {
                    return ResponseEntity.notFound().build();
                }
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
