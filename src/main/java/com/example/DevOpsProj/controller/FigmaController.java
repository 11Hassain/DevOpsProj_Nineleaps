package com.example.DevOpsProj.controller;

import com.example.DevOpsProj.dto.responseDto.FigmaDTO;
import com.example.DevOpsProj.model.Figma;
import com.example.DevOpsProj.repository.FigmaRepository;
import com.example.DevOpsProj.repository.UserRepository;
import com.example.DevOpsProj.service.FigmaService;
import com.example.DevOpsProj.service.JwtService;
import com.example.DevOpsProj.service.ProjectService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.apache.commons.io.IOUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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

    @PutMapping("/{figmaId}/user")//add user and screenshot to figma
    public ResponseEntity<String> addUserToFigma(@PathVariable("figmaId") Long figmaId,
                                                 @RequestBody FigmaDTO figmaDTO,
                                                 @RequestHeader("AccessToken") String accessToken) {
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            try {
                Optional<Figma> optionalFigma = figmaRepository.findById(figmaId);
                if (optionalFigma.isPresent()) {
                    Figma figma = optionalFigma.get();
                    figma.setUser(figmaDTO.getUser());
                    figma.setScreenshotImage(figmaDTO.getScreenshotImage());
                    figmaRepository.save(figma);
                    return ResponseEntity.ok("User added");
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
    public ResponseEntity<?> downloadScreenshotsForFigma(@PathVariable("figmaId") Long figmaId) {
        Optional<Figma> optionalFigma = figmaRepository.findById(figmaId);
        if (optionalFigma.isPresent()) {
            Figma figma = optionalFigma.get();
            String screenshotImage = figma.getScreenshotImage();

            // Check if there is any screenshot image to download
            if (screenshotImage == null || screenshotImage.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            try {
                if (screenshotImage.startsWith("data:")) {

                    String base64Data = screenshotImage.substring(screenshotImage.indexOf(",") + 1);
                    byte[] imageBytes = Base64.getDecoder().decode(base64Data);
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.IMAGE_PNG);
                    headers.setContentDispositionFormData("attachment", "screenshot.png");

                    return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
                } else {
                    return ResponseEntity.notFound().build();
                }
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }

        return ResponseEntity.notFound().build();
    }

}







