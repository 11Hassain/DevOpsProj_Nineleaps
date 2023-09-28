package com.example.devopsproj.Controller;

import com.example.devopsproj.controller.AccessRequestController;
import com.example.devopsproj.controller.FigmaController;
import com.example.devopsproj.dto.requestdto.AccessRequestDTO;
import com.example.devopsproj.dto.responsedto.FigmaDTO;
import com.example.devopsproj.dto.responsedto.ProjectDTO;
import com.example.devopsproj.exceptions.FigmaNotFoundException;
import com.example.devopsproj.exceptions.FigmaServiceException;
import com.example.devopsproj.model.Figma;
import com.example.devopsproj.model.Project;
import com.example.devopsproj.repository.FigmaRepository;
import com.example.devopsproj.repository.ProjectRepository;
import com.example.devopsproj.service.interfaces.AccessRequestService;
import com.example.devopsproj.service.interfaces.FigmaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class FigmaControllerTest {
    private MockMvc mockMvc;

    private FigmaController figmaController;
    private FigmaService figmaService;
    private FigmaRepository figmaRepository;
    private ProjectRepository projectRepository;

    @BeforeEach
    public void setUp() {
        figmaService = mock(FigmaService.class);
        projectRepository = mock(ProjectRepository.class);
        figmaController = new FigmaController(figmaService, projectRepository);
        mockMvc = MockMvcBuilders.standaloneSetup(figmaController).build();

    }


    @Test
    public void testCreateFigmaSuccess() {
        // Create a sample FigmaDTO
        FigmaDTO figmaDTO = new FigmaDTO();
        // Set properties in figmaDTO

        // Mock the figmaService.createFigma() method to return a Figma object
        Figma mockFigma = new Figma();
        when(figmaService.createFigma(figmaDTO)).thenReturn(mockFigma);

        // Call the controller method
        ResponseEntity<String> response = figmaController.createFigma(figmaDTO);

        // Verify that the response is successful and contains the expected message
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Figma created successfully", response.getBody());
    }

    @Test
    public void testCreateFigmaDataIntegrityViolationException() {
        // Create a sample FigmaDTO
        FigmaDTO figmaDTO = new FigmaDTO();
        // Set properties in figmaDTO

        // Mock the figmaService.createFigma() method to throw a DataIntegrityViolationException
        when(figmaService.createFigma(figmaDTO)).thenThrow(new DataIntegrityViolationException("Test exception"));

        // Call the controller method
        assertThrows(DataIntegrityViolationException.class, () -> {
            figmaController.createFigma(figmaDTO);
        });
    }

    @Test
    public void testCreateFigmaRuntimeException() {
        // Create a sample FigmaDTO
        FigmaDTO figmaDTO = new FigmaDTO();
        // Set properties in figmaDTO

        // Mock the figmaService.createFigma() method to throw a RuntimeException
        when(figmaService.createFigma(figmaDTO)).thenThrow(new RuntimeException("Test exception"));

        // Call the controller method
        assertThrows(RuntimeException.class, () -> {
            figmaController.createFigma(figmaDTO);
        });
    }


//    @Test
//    public void testGetAllFigmaProjectsSuccess() {
//        // Create a list of mock Project objects and corresponding Figma objects
//        List<Project> mockProjects = new ArrayList<>();
//        List<Figma> mockFigmas = new ArrayList<>();
//
//        // Create and add mock projects and figmas to the lists
//        for (int i = 1; i <= 3; i++) {
//            Project mockProject = new Project();
//            Figma mockFigma = new Figma();
//            mockFigma.setFigmaId((long) i);
//            mockFigma.setProject(mockProject);
//            mockFigma.setFigmaURL("FigmaURL" + i);
//            mockProject.setFigma(mockFigma);
//            mockProjects.add(mockProject);
//            mockFigmas.add(mockFigma);
//        }
//
//        // Mock the projectRepository.findAllProjects() method to return the list of mock projects
//        when(projectRepository.findAllProjects()).thenReturn(mockProjects);
//
//        // Call the controller method
//        ResponseEntity<List<FigmaDTO>> response = figmaController.getAllFigmaProjects();
//
//        // Debugging: Print the contents of mockProjects and response
//        System.out.println("Mock Projects: " + mockProjects);
//        System.out.println("Response: " + response.getBody());
//
//        // Verify that the response is successful and contains the expected number of FigmaDTOs
//        assertEquals(200, response.getStatusCodeValue());
//        List<FigmaDTO> figmaDTOs = response.getBody();
//        assertNotNull(figmaDTOs);
//     //  assertEquals(3, figmaDTOs.size());
//    }
//
//
//    @Test
//    public void testGetAllFigmaProjectsNoFigmaObjects() {
//        // Create a list of mock Project objects without associated Figma objects
//        List<Project> mockProjects = new ArrayList<>();
//
//        // Mock the projectRepository.findAllProjects() method to return the list of mock projects
//        when(projectRepository.findAllProjects()).thenReturn(mockProjects);
//
//        // Call the controller method
//        ResponseEntity<List<FigmaDTO>> response = figmaController.getAllFigmaProjects();
//
//        // Verify that the response is successful and contains an empty list of FigmaDTOs
//        assertEquals(200, response.getStatusCodeValue());
//        List<FigmaDTO> figmaDTOs = response.getBody();
//        assertNotNull(figmaDTOs);
//        assertTrue(figmaDTOs.isEmpty());
//    }




    @Test
    public void testDeleteFigma_Success() {
        Long figmaId = 1L;

        // Mock the service method to perform a successful deletion
        doNothing().when(figmaService).deleteFigma(figmaId);

        // Call the controller method
        ResponseEntity<String> response = figmaController.deleteFigma(figmaId);

        // Verify that the service method was called with the correct argument
        verify(figmaService, times(1)).deleteFigma(figmaId);

        // Verify the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Figma deleted successfully", response.getBody());
    }

    @Test
    public void testGetFigmaByProjectId_Success() {
        Long projectId = 1L;
        String figmaURL = "https://example.com/figma";

        // Mock the service method to return a figmaURL
        when(figmaService.getFigmaURLByProjectId(projectId)).thenReturn(figmaURL);

        // Call the controller method
        ResponseEntity<Object> response = figmaController.getFigmaByProjectId(projectId);

        // Verify that the service method was called with the correct argument
        verify(figmaService, times(1)).getFigmaURLByProjectId(projectId);

        // Verify the response
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(figmaURL, response.getBody());
    }

    @Test
    public void testGetFigmaByProjectId_FigmaNotFound() {
        Long projectId = 2L;

        // Mock the service method to return null, indicating no Figma found
        when(figmaService.getFigmaURLByProjectId(projectId)).thenReturn(null);

        // Call the controller method
        ResponseEntity<Object> response = figmaController.getFigmaByProjectId(projectId);

        // Verify that the service method was called with the correct argument
        verify(figmaService, times(1)).getFigmaURLByProjectId(projectId);

        // Verify the response
        assertEquals(200, response.getStatusCodeValue());
    }
    @Test
    public void testGetFigma_Success() {
        Long figmaId = 1L;
        FigmaDTO expectedFigmaDTO = new FigmaDTO(new ProjectDTO(/* populate project DTO */), "https://example.com/figma");

        // Mock the service method to return an Optional containing a FigmaDTO
        when(figmaService.getFigmaById(figmaId)).thenReturn(Optional.of(expectedFigmaDTO));

        // Call the controller method
        ResponseEntity<Object> response = figmaController.getFigma(figmaId);

        // Verify that the service method was called with the correct argument
        verify(figmaService, times(1)).getFigmaById(figmaId);

        // Verify the response
        assertEquals(200, response.getStatusCodeValue());

        // Check if the response body is an instance of Optional
        assertTrue(response.getBody() instanceof Optional);

        // Cast the response body to Optional<FigmaDTO>
        Optional<FigmaDTO> responseBody = (Optional<FigmaDTO>) response.getBody();

        // Check if the Optional is present
        assertTrue(responseBody.isPresent());

        // Get the value from the Optional and compare
        assertEquals(expectedFigmaDTO, responseBody.get());
    }

    @Test
    public void testGetFigma_FigmaNotFound() {
        Long figmaId = 2L;

        // Mock the service method to return an empty Optional, indicating no Figma found
        when(figmaService.getFigmaById(figmaId)).thenReturn(Optional.empty());

        // Call the controller method
        ResponseEntity<Object> response = figmaController.getFigma(figmaId);

        // Verify that the service method was called with the correct argument
        verify(figmaService, times(1)).getFigmaById(figmaId);

        // Verify the response
        assertEquals(200, response.getStatusCodeValue());

        // Check if the response body is an instance of Optional
        assertTrue(response.getBody() instanceof Optional);

        // Cast the response body to Optional<FigmaDTO>
        Optional<FigmaDTO> responseBody = (Optional<FigmaDTO>) response.getBody();

        // Check if the Optional is empty
        assertFalse(responseBody.isPresent());
    }



}

