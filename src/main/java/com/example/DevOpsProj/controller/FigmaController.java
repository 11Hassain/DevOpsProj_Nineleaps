package com.example.DevOpsProj.controller;

import com.example.DevOpsProj.dto.responseDto.FigmaDTO;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
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
            if (optionalFigmaDTO.isPresent()){
                return ResponseEntity.ok(optionalFigmaDTO);
            }
            else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Token");
        }
    }

    @PutMapping("/{figmaId}/user")//add user and screenshot to figma
    public ResponseEntity<String> addUserToFigma(@PathVariable("figmaId") Long figmaId,
                                                 @RequestBody FigmaDTO figmaDTO,
                                                 @RequestHeader("AccessToken") String accessToken){
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            try{
                Optional<Figma> optionalFigma = figmaRepository.findById(figmaId);
                if(optionalFigma.isPresent()){
                    Figma figma = optionalFigma.get();
                    figma.setUser(figmaDTO.getUser());
                    figma.setScreenshotImage(figmaDTO.getScreenshotImage());
                    figmaRepository.save(figma);
                    return ResponseEntity.ok("User added");
                }else {
                    return ResponseEntity.notFound().build();
                }
            }catch (Exception e){
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Token");
        }
    }

}



