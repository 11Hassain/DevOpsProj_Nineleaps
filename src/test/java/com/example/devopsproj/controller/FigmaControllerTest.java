package com.example.devopsproj.controller;

import com.example.devopsproj.dto.responsedto.FigmaDTO;
import com.example.devopsproj.dto.responsedto.FigmaScreenshotDTO;
import com.example.devopsproj.dto.responsedto.ProjectDTO;
import com.example.devopsproj.exceptions.NotFoundException;
import com.example.devopsproj.model.Figma;
import com.example.devopsproj.model.Project;
import com.example.devopsproj.service.implementations.FigmaServiceImpl;
import com.example.devopsproj.service.implementations.JwtServiceImpl;
import com.example.devopsproj.utils.DTOModelMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

class FigmaControllerTest {

    @InjectMocks
    private FigmaController figmaController;
    @Mock
    private FigmaServiceImpl figmaService;
    @Mock
    private JwtServiceImpl jwtService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    class CreateFigmaTest {
        @Test
        @DisplayName("Testing success case with valid token")
        void testCreateFigma_ValidToken_Success() {
            FigmaDTO figmaDTO = new FigmaDTO();
            figmaDTO.setFigmaURL("https://www.figmaURL.com/");

            when(jwtService.isTokenTrue(anyString())).thenReturn(true);

            ResponseEntity<String> response = figmaController.createFigma(figmaDTO, "valid-access-token");

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("Figma created successfully", response.getBody());
        }

        @Test
        @DisplayName("Testing duplicate name case")
        void testCreateFigma_DuplicateName() {
            FigmaDTO figmaDTO = new FigmaDTO();
            figmaDTO.setFigmaURL("https://www.figmaURL.com/");

            when(jwtService.isTokenTrue(anyString())).thenReturn(true);
            doThrow(DataIntegrityViolationException.class).when(figmaService).createFigma(figmaDTO);

            ResponseEntity<String> response = figmaController.createFigma(figmaDTO, "valid-access-token");

            assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
            assertEquals("Could not create figma", response.getBody());
        }

        @Test
        @DisplayName("Testing failure case with invalid token")
        void testCreateFigma_InvalidToken(){
            FigmaDTO figmaDTO = new FigmaDTO();
            figmaDTO.setFigmaURL("https://www.figmaURL.com/");

            when(jwtService.isTokenTrue(anyString())).thenReturn(false);

            ResponseEntity<String> response = figmaController.createFigma(figmaDTO, "invalid-access-token");

            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            assertEquals("Invalid Token", response.getBody());
        }
    }

    @Nested
    class GetAllFigmaProjectsTest {
        @Test
        @DisplayName("Testing success case with valid token")
        void testGetAllFigmaProjects_ValidToken() {
            List<Figma> figmaProjects = new ArrayList<>();
            figmaProjects.add(new Figma());
            figmaProjects.add(new Figma());

            when(jwtService.isTokenTrue(anyString())).thenReturn(true);
            when(figmaService.getAllFigmaProjects()).thenReturn(figmaProjects);

            ResponseEntity<Object> response = figmaController.getAllFigmaProjects("valid-access-token");

            assertEquals(HttpStatus.OK, response.getStatusCode());

            // Retrieve the list of FigmaDTOs from the response
            List<FigmaDTO> responseFigmaDTOs = (List<FigmaDTO>) response.getBody();

            assertNotNull(responseFigmaDTOs);
            assertEquals(2, responseFigmaDTOs.size());
        }

        @Test
        @DisplayName("Testing empty projects list case")
        void testGetAllFigmaProjects_EmptyList() {
            when(jwtService.isTokenTrue(anyString())).thenReturn(true);
            when(figmaService.getAllFigmaProjects()).thenReturn(Collections.emptyList());

            ResponseEntity<Object> response = figmaController.getAllFigmaProjects("valid-access-token");

            assertEquals(HttpStatus.OK, response.getStatusCode());

            // Retrieve the list of FigmaDTOs from the response
            List<FigmaDTO> responseFigmaDTOs = (List<FigmaDTO>) response.getBody();

            assertNotNull(responseFigmaDTOs);
            assertTrue(responseFigmaDTOs.isEmpty());
        }

        @Test
        @DisplayName("Testing project not null case")
        void testGetAllFigmaProjects_ProjectNotNull() {
            String accessToken = "valid_access_token";

            when(jwtService.isTokenTrue(accessToken)).thenReturn(true);

            List<Figma> mockFigmaProjects = new ArrayList<>();
            Project mockProject = new Project();
            mockProject.setProjectId(1L);
            mockProject.setProjectName("Test Project");
            Figma mockFigma = new Figma();
            mockFigma.setFigmaId(1L);
            mockFigma.setProject(mockProject);
            mockFigma.setFigmaURL("https://example.com/figma1");
            mockFigmaProjects.add(mockFigma);

            when(figmaService.getAllFigmaProjects()).thenReturn(mockFigmaProjects);

            ResponseEntity<Object> response = figmaController.getAllFigmaProjects(accessToken);

            assertEquals(HttpStatus.OK, response.getStatusCode());

            List<FigmaDTO> responseDTOs = (List<FigmaDTO>) response.getBody();
            assertNotNull(responseDTOs);
            assertEquals(1, responseDTOs.size());
        }

        @Test
        @DisplayName("Testing failure case with invalid token")
        void testGetAllFigmaProjects_InvalidToken(){
            when(jwtService.isTokenTrue(anyString())).thenReturn(false);

            ResponseEntity<Object> response = figmaController.getAllFigmaProjects("invalid-access-token");

            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            assertEquals("Invalid Token", response.getBody());
        }
    }

    @Nested
    class GetFigmaTest {
        @Test
        @DisplayName("Testing success case with valid token")
        void testGetFigma_ValidToken_ExistingFigma() {
            Long figmaId = 1L;

            FigmaDTO expectedFigmaDTO = new FigmaDTO();
            expectedFigmaDTO.setFigmaId(figmaId);

            when(jwtService.isTokenTrue(anyString())).thenReturn(true);
            when(figmaService.getFigmaById(figmaId)).thenReturn(Optional.of(expectedFigmaDTO));

            ResponseEntity<Object> response = figmaController.getFigma(figmaId, "valid-access-token");

            assertEquals(HttpStatus.OK, response.getStatusCode());

            // Retrieve the Optional<FigmaDTO> from the response body
            Optional<FigmaDTO> actualFigmaDTOOptional = (Optional<FigmaDTO>) response.getBody();

            assertNotNull(actualFigmaDTOOptional);
            assertTrue(actualFigmaDTOOptional.isPresent());

            FigmaDTO actualFigmaDTO = actualFigmaDTOOptional.get();

            assertEquals(expectedFigmaDTO, actualFigmaDTO);
        }

        @Test
        @DisplayName("Testing Figma non-existent case")
        void testGetFigma_ValidToken_NonExistingFigma() {
            Long figmaId = 1L;
            when(jwtService.isTokenTrue(anyString())).thenReturn(true);

            when(figmaService.getFigmaById(figmaId)).thenReturn(Optional.empty());

            ResponseEntity<Object> response = figmaController.getFigma(figmaId, "valid-access-token");

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertNull(response.getBody());
        }

        @Test
        @DisplayName("Testing failure case with invalid token")
        void testGetFigma_InvalidToken(){
            Long figmaId = 1L;

            when(jwtService.isTokenTrue(anyString())).thenReturn(false);

            ResponseEntity<Object> response = figmaController.getFigma(figmaId,"invalid-access-token");

            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            assertEquals("Invalid Token", response.getBody());
        }
    }

    @Nested
    class AddUserAndScreenshotsToFigmaTest {
        @Test
        @DisplayName("Testing success case with valid token")
        void testAddUserAndScreenshotsToFigma_ValidToken_Success() {
            Long figmaId = 1L;
            FigmaDTO figmaDTO = new FigmaDTO();
            String accessToken = "valid-access-token";
            String expectedResult = "User and screenshots added successfully";

            when(jwtService.isTokenTrue(accessToken)).thenReturn(true);
            when(figmaService.saveUserAndScreenshotsToFigma(figmaId, figmaDTO)).thenReturn(expectedResult);

            ResponseEntity<String> response = figmaController.addUserAndScreenshotsToFigma(figmaId, figmaDTO, accessToken);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(expectedResult, response.getBody());
        }

        @Test
        @DisplayName("Testing Figma not found case")
        void testAddUserAndScreenshotsToFigma_ValidToken_NotFound() {
            Long figmaId = 1L;
            FigmaDTO figmaDTO = new FigmaDTO();
            String accessToken = "valid-access-token";
            String errorMessage = "Figma not found";

            when(jwtService.isTokenTrue(accessToken)).thenReturn(true);
            when(figmaService.saveUserAndScreenshotsToFigma(figmaId, figmaDTO)).thenThrow(new NotFoundException(errorMessage));

            ResponseEntity<String> response = figmaController.addUserAndScreenshotsToFigma(figmaId, figmaDTO, accessToken);

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertEquals(errorMessage, response.getBody());
        }

        @Test
        @DisplayName("Testing Internal server error case")
        void testAddUserAndScreenshotsToFigma_ValidToken_InternalServerError() {
            Long figmaId = 1L;
            FigmaDTO figmaDTO = new FigmaDTO();
            String accessToken = "valid-access-token";
            String errorMessage = "Internal server error occurred";

            when(jwtService.isTokenTrue(accessToken)).thenReturn(true);
            when(figmaService.saveUserAndScreenshotsToFigma(figmaId, figmaDTO)).thenThrow(new RuntimeException(errorMessage));

            ResponseEntity<String> response = figmaController.addUserAndScreenshotsToFigma(figmaId, figmaDTO, accessToken);

            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
            assertEquals(errorMessage, response.getBody());
        }

        @Test
        @DisplayName("Testing failure case with invalid token")
        void testAddUserAndScreenshotsToFigma_InvalidToken(){
            Long figmaId = 1L;
            FigmaDTO figmaDTO = new FigmaDTO();
            figmaDTO.setFigmaURL("https://www.figmaURL.com/");

            when(jwtService.isTokenTrue(anyString())).thenReturn(false);

            ResponseEntity<String> response = figmaController.addUserAndScreenshotsToFigma(figmaId,figmaDTO,"invalid-access-token");

            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            assertEquals("Invalid Token", response.getBody());
        }
    }

    @Nested
    class DeleteFigmaTest {
        @Test
        @DisplayName("Testing success case with valid token")
        void testDeleteFigma_ValidToken_Success() {
            Long figmaId = 1L;
            String accessToken = "valid-access-token";

            when(jwtService.isTokenTrue(accessToken)).thenReturn(true);

            ResponseEntity<String> response = figmaController.deleteFigma(figmaId, accessToken);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("Figma deleted successfully", response.getBody());
        }

        @Test
        @DisplayName("Testing failure case with invalid token")
        void testDeleteFigma_InvalidToken(){
            Long figmaId = 1L;

            when(jwtService.isTokenTrue(anyString())).thenReturn(false);

            ResponseEntity<String> response = figmaController.deleteFigma(figmaId,"invalid-access-token");

            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            assertEquals("Invalid Token", response.getBody());
        }
    }

    @Nested
    class GetFigmaByProjectIdTest {
        @Test
        @DisplayName("Testing success case with valid token")
        void testGetFigmaByProjectId_ValidToken_FoundFigmaURL() {
            Long projectId = 1L;
            String accessToken = "valid-access-token";
            String expectedFigmaURL = "https://example.com/figma";

            when(jwtService.isTokenTrue(accessToken)).thenReturn(true);
            when(figmaService.getFigmaURLByProjectId(projectId)).thenReturn(expectedFigmaURL);

            ResponseEntity<Object> response = figmaController.getFigmaByProjectId(projectId, accessToken);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(expectedFigmaURL, response.getBody());
        }

        @Test
        @DisplayName("Testing Figma URL not found case")
        void testGetFigmaByProjectId_ValidToken_NotFoundFigmaURL() {
            Long projectId = 1L;
            String accessToken = "valid-access-token";

            when(jwtService.isTokenTrue(accessToken)).thenReturn(true);
            when(figmaService.getFigmaURLByProjectId(projectId)).thenThrow(new NotFoundException("Figma URL not found"));

            ResponseEntity<Object> response = figmaController.getFigmaByProjectId(projectId, accessToken);

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertNull(response.getBody());
        }

        @Test
        @DisplayName("Testing failure case with invalid token")
        void testGetFigmaByProjectId_InvalidToken(){
            Long figmaId = 1L;

            when(jwtService.isTokenTrue(anyString())).thenReturn(false);

            ResponseEntity<Object> response = figmaController.getFigmaByProjectId(figmaId,"invalid-access-token");

            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            assertEquals("Invalid Token", response.getBody());
        }
    }

    @Nested
    class GetScreenshotsForFigmaIdTest {
        @Test
        @DisplayName("Testing success case with valid token")
        void testGetScreenshotsForFigmaId_ValidToken_FoundScreenshots() {
            Long figmaId = 1L;
            String accessToken = "valid-access-token";
            String user1 = "user 1";
            String user2 = "user 2";

            List<FigmaScreenshotDTO> expectedScreenshots = Arrays.asList(
                    new FigmaScreenshotDTO(user1, "Screenshot 1"),
                    new FigmaScreenshotDTO(user2, "Screenshot 2")
            );

            when(jwtService.isTokenTrue(accessToken)).thenReturn(true);
            when(figmaService.getScreenshotsByFigmaId(figmaId)).thenReturn(expectedScreenshots);

            ResponseEntity<Object> response = figmaController.getScreenshotsForFigmaId(figmaId, accessToken);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(expectedScreenshots, response.getBody());
        }

        @Test
        @DisplayName("Testing screenshot not found case")
        void testGetScreenshotsForFigmaId_ValidToken_NotFoundScreenshots() {
            Long figmaId = 1L;
            String accessToken = "valid-access-token";

            when(jwtService.isTokenTrue(accessToken)).thenReturn(true);
            when(figmaService.getScreenshotsByFigmaId(figmaId)).thenThrow(new NotFoundException("Screenshots not found"));

            ResponseEntity<Object> response = figmaController.getScreenshotsForFigmaId(figmaId, accessToken);

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertNull(response.getBody());
        }

        @Test
        @DisplayName("Testing internal server error case")
        void testGetScreenshotsForFigmaId_ValidToken_InternalServerError() {
            Long figmaId = 1L;
            String accessToken = "valid-access-token";

            when(jwtService.isTokenTrue(accessToken)).thenReturn(true);
            when(figmaService.getScreenshotsByFigmaId(figmaId)).thenThrow(new RuntimeException("Internal server error"));

            ResponseEntity<Object> response = figmaController.getScreenshotsForFigmaId(figmaId, accessToken);

            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
            assertNull(response.getBody());
        }

        @Test
        @DisplayName("Testing failure case with invalid token")
        void testGetScreenshotsForFigmaId_InvalidToken(){
            Long figmaId = 1L;

            when(jwtService.isTokenTrue(anyString())).thenReturn(false);

            ResponseEntity<Object> response = figmaController.getScreenshotsForFigmaId(figmaId,"invalid-access-token");

            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            assertEquals("Invalid Token", response.getBody());
        }
    }

}