package com.example.DevOpsProj.controller;

import com.example.DevOpsProj.dto.responseDto.FigmaDTO;
import com.example.DevOpsProj.dto.responseDto.FigmaScreenshotDTO;
import com.example.DevOpsProj.model.Figma;
import com.example.DevOpsProj.repository.FigmaRepository;
import com.example.DevOpsProj.repository.UserRepository;
import com.example.DevOpsProj.service.FigmaService;
import com.example.DevOpsProj.service.JwtService;
import com.example.DevOpsProj.service.ProjectService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.util.*;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/figmas")
public class FigmaController {
    @Autowired
    private FigmaService figmaService;
    @Autowired
    private FigmaRepository figmaRepository;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtService jwtService;

    @PostMapping("/create")
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
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Token");
        }
    }

    @GetMapping("/getAll")
    public ResponseEntity<Object> getAllFigmaProjects(@RequestHeader("AccessToken") String accessToken) {
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            List<Figma> figmaProjects = figmaService.getAllFigmaProjects();

            List<FigmaDTO> figmaDTOs = figmaProjects.stream()
                    .filter(figma -> figma != null) // Filter out null values
                    .map(figma -> new FigmaDTO(
                            figma.getFigmaId(),
                            figmaService.mapProjectToProjectDTO(figma.getProject()),
                            figma.getFigmaURL()))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(figmaDTOs);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Token");
        }
    }


    @GetMapping("/get/{figmaId}")
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
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Token");
        }
    }

    @PostMapping("/{figmaId}/user")
    public ResponseEntity<String> addUserAndScreenshotsToFigma(@PathVariable("figmaId") Long figmaId,
                                                               @RequestBody FigmaDTO figmaDTO,
                                                               @RequestHeader("AccessToken") String accessToken) {

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
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Token");
        }
    }


    @DeleteMapping("/{figmaId}")
    public ResponseEntity<String> deleteFigma(@PathVariable Long figmaId) {
        figmaService.deleteFigma(figmaId);
        return ResponseEntity.ok("Figma deleted successfully");
    }

    @GetMapping("/project/{projectId}")
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
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Token");
        }
    }


    @GetMapping("/{figmaId}/screenshots")
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
<<<<<<< HEAD
                        screenshotDTO.setScreenshotImageURL(entry.getValue());
=======
                        screenshotDTO.setScreenshotImageURL(entry.getValue()); // Set the URL here
>>>>>>> c66d5cae5148ffa4a0b83af05c7baf47f0d56665
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
