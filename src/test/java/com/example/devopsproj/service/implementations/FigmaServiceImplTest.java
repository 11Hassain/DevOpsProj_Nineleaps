package com.example.devopsproj.service.implementations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.example.devopsproj.dto.responsedto.FigmaDTO;
import com.example.devopsproj.dto.responsedto.FigmaScreenshotDTO;
import com.example.devopsproj.dto.responsedto.ProjectDTO;
import com.example.devopsproj.exceptions.FigmaCreationException;
import com.example.devopsproj.exceptions.FigmaNotFoundException;
import com.example.devopsproj.exceptions.FigmaServiceException;
import com.example.devopsproj.model.Figma;
import com.example.devopsproj.model.Project;
import com.example.devopsproj.repository.FigmaRepository;
import com.example.devopsproj.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.*;

public class FigmaServiceImplTest {

    @Mock
    private FigmaRepository figmaRepository;

    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private ProjectServiceImpl projectService;

    @InjectMocks
    private FigmaServiceImpl figmaService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }
//    @Test
//    public void testCreateFigma_Success() throws FigmaCreationException {
//        // Arrange
//        FigmaDTO figmaDTO = new FigmaDTO("https://example.com/figma");
//
//        // Initialize ProjectDTO with required properties
//        ProjectDTO projectDTO = new ProjectDTO();
//        projectDTO.setProjectId(1L); // Set the project ID
//        projectDTO.setProjectName("Test Project");
//        projectDTO.setProjectDescription("Test Description");
//
//        // Set the projectDTO property of figmaDTO
//        figmaDTO.setProjectDTO(projectDTO);
//
//        // Mock the mapProjectDTOToProject method to return a Project
//        when(projectService.mapProjectDTOToProject(eq(projectDTO))).thenReturn(new Project());
//
//        // Mock the figmaRepository save method to return the Figma object
//        when(figmaRepository.save(any(Figma.class))).thenReturn(new Figma());
//
//        // Act
//        Figma createdFigma = figmaService.createFigma(figmaDTO);
//
//        // Assert
//        assertNotNull(createdFigma);
//        assertEquals("https://example.com/figma", createdFigma.getFigmaURL());
//        // Add additional assertions as needed
//    }

//
//    @Test
//    public void testCreateFigma_Failure() {
//        // Arrange
//        FigmaDTO figmaDTO = new FigmaDTO("https://example.com/figma");
//
//        // Mock the mapProjectDTOToProject method to throw an exception
//        when(figmaService.mapProjectDTOToProject(any(ProjectDTO.class))).thenThrow(new RuntimeException("Test Exception"));
//
//        // Act and Assert
//        assertThrows(FigmaCreationException.class, () -> figmaService.createFigma(figmaDTO));
//    }

    @Test
    public void testGetAllFigmaProjects_Success() {
        // Arrange
        Project project1 = new Project();
        Figma figma1 = new Figma();
        figma1.setProject(project1);

        Project project2 = new Project();
        Figma figma2 = new Figma();
        figma2.setProject(project2);

        // Mock the projectRepository findAllProjects method to return a list of projects
        when(projectRepository.findAllProjects()).thenReturn(List.of(project1, project2));

        // Mock the figmaRepository findFigmaByProjectId method for each project
        when(figmaRepository.findFigmaByProjectId(eq(project1.getProjectId()))).thenReturn(Optional.of(figma1));
        when(figmaRepository.findFigmaByProjectId(eq(project2.getProjectId()))).thenReturn(Optional.of(figma2));

        // Act
        List<Figma> figmaProjects = figmaService.getAllFigmaProjects();

        // Assert
        assertNotNull(figmaProjects);
        assertEquals(2, figmaProjects.size());
        // Add additional assertions as needed
    }

    @Test
    public void testGetAllFigmaProjects_EmptyList() {
        // Arrange
        // Mock the projectRepository findAllProjects method to return an empty list
        when(projectRepository.findAllProjects()).thenReturn(Collections.emptyList());

        // Act
        List<Figma> figmaProjects = figmaService.getAllFigmaProjects();

        // Assert
        assertNotNull(figmaProjects);
        assertTrue(figmaProjects.isEmpty());
    }


    @Test
    public void testGetFigmaById_FigmaNotFound() {
        // Arrange
        Long figmaId = 1L;

        // Mock the figmaRepository.findById method to return an empty Optional
        when(figmaRepository.findById(figmaId)).thenReturn(Optional.empty());

        // Act
        Optional<FigmaDTO> result = figmaService.getFigmaById(figmaId);

        // Assert
        assertFalse(result.isPresent());
    }


    @Test
    public void testDeleteFigma_Success() {
        // Arrange
        Long figmaId = 1L;

        // Mock the figmaRepository.deleteById method
        doNothing().when(figmaRepository).deleteById(figmaId);

        // Act and Assert
        assertDoesNotThrow(() -> figmaService.deleteFigma(figmaId));
    }

    @Test
    public void testDeleteFigma_FigmaNotFound() {
        // Arrange
        Long figmaId = 1L;

        // Mock the figmaRepository.deleteById method to throw EmptyResultDataAccessException
        doThrow(EmptyResultDataAccessException.class).when(figmaRepository).deleteById(figmaId);

        // Act and Assert
        assertThrows(FigmaNotFoundException.class, () -> figmaService.deleteFigma(figmaId));
    }

    @Test
    public void testDeleteFigma_ExceptionOccurred() {
        // Arrange
        Long figmaId = 1L;

        // Mock the figmaRepository.deleteById method to throw a runtime exception
        doThrow(FigmaServiceException.class).when(figmaRepository).deleteById(figmaId);

        // Act and Assert
        assertThrows(FigmaServiceException.class, () -> figmaService.deleteFigma(figmaId));
    }

    @Test
    public void testGetFigmaURLByProjectId_FigmaFound() {
        // Arrange
        Long projectId = 1L;
        String figmaURL = "https://example.com/figma";
        Figma figma = new Figma(); // Create a new Figma object
        figma.setFigmaURL(figmaURL); // Set the figmaURL using the setter method

        // Mock the figmaRepository.findFigmaByProjectId method to return a Figma object
        when(figmaRepository.findFigmaByProjectId(eq(projectId))).thenReturn(Optional.of(figma));

        // Act
        String result = figmaService.getFigmaURLByProjectId(projectId);

        // Assert
        assertNotNull(result);
        assertEquals(figmaURL, result);
    }

    @Test
    public void testGetFigmaURLByProjectId_FigmaNotFound() {
        // Arrange
        Long projectId = 1L;

        // Mock the figmaRepository.findFigmaByProjectId method to return an empty Optional
        when(figmaRepository.findFigmaByProjectId(eq(projectId))).thenReturn(Optional.empty());

        // Act
        String result = figmaService.getFigmaURLByProjectId(projectId);

        // Assert
        assertNull(result);
    }


    @Test
    public void testGetScreenshotsForFigmaIdWithScreenshots_Success() {
        // Arrange
        Long figmaId = 1L;
        Map<String, String> screenshotImagesByUser = new HashMap<>();
        screenshotImagesByUser.put("User1", "image1.jpg");
        screenshotImagesByUser.put("User2", "image2.jpg");

        Figma figma = new Figma();
        figma.setScreenshotImagesByUser(screenshotImagesByUser);

        when(figmaRepository.findById(figmaId)).thenReturn(Optional.of(figma));

        // Act
        List<FigmaScreenshotDTO> screenshotDTOList = figmaService.getScreenshotsForFigmaId(figmaId);

        // Assert
        assertNotNull(screenshotDTOList);
        assertEquals(2, screenshotDTOList.size());
        // Add additional assertions as needed
    }

    @Test
    public void testGetScreenshotsForFigmaIdWithNoScreenshots_Success() {
        // Arrange
        Long figmaId = 1L;
        Figma figma = new Figma();
        figma.setScreenshotImagesByUser(null); // No screenshots

        when(figmaRepository.findById(figmaId)).thenReturn(Optional.of(figma));

        // Act
        List<FigmaScreenshotDTO> screenshotDTOList = figmaService.getScreenshotsForFigmaId(figmaId);

        // Assert
        assertNotNull(screenshotDTOList);
        assertEquals(0, screenshotDTOList.size());
    }

    @Test
    public void testGetScreenshotsForFigmaIdWithEmptyScreenshots_Success() {
        // Arrange
        Long figmaId = 1L;
        Figma figma = new Figma();
        figma.setScreenshotImagesByUser(Collections.emptyMap()); // No screenshots

        when(figmaRepository.findById(figmaId)).thenReturn(Optional.of(figma));

        // Act
        List<FigmaScreenshotDTO> screenshotDTOList = figmaService.getScreenshotsForFigmaId(figmaId);

        // Assert
        assertNotNull(screenshotDTOList);
        assertEquals(0, screenshotDTOList.size());
    }

    @Test
    public void testGetScreenshotsForFigmaIdWithFigmaNotFound_Success() {
        // Arrange
        Long figmaId = 1L;
        when(figmaRepository.findById(figmaId)).thenReturn(Optional.empty()); // Figma not found

        // Act
        List<FigmaScreenshotDTO> screenshotDTOList = figmaService.getScreenshotsForFigmaId(figmaId);

        // Assert
        assertNotNull(screenshotDTOList);
        assertEquals(0, screenshotDTOList.size());
    }
    @Test
    public void testGetAllFigmaDTOs_Success() {
        // Arrange
        Project project1 = new Project();
        project1.setProjectId(1L);
        project1.setProjectName("Project 1");
        Figma figma1 = new Figma();
        figma1.setFigmaId(101L);
        figma1.setProject(project1);
        figma1.setFigmaURL("https://example.com/figma1");

        Project project2 = new Project();
        project2.setProjectId(2L);
        project2.setProjectName("Project 2");
        Figma figma2 = new Figma();
        figma2.setFigmaId(102L);
        figma2.setProject(project2);
        figma2.setFigmaURL("https://example.com/figma2");

        // Mock the projectRepository.findAllProjects() method to return the list of active projects
        when(projectRepository.findAllProjects()).thenReturn(List.of(project1, project2));

        // Act
        List<FigmaDTO> figmaDTOs = figmaService.getAllFigmaDTOs();

        // Assert
        assertEquals(0, figmaDTOs.size());
        // Add additional assertions as needed
    }


    @Test
    public void testGetAllFigmaDTOs_NoActiveProjects_Success() {
        // Arrange
        when(projectRepository.findAllProjects()).thenReturn(new ArrayList<>());

        // Act
        List<FigmaDTO> figmaDTOs = figmaService.getAllFigmaDTOs();

        // Assert
        assertEquals(0, figmaDTOs.size());
    }

    @Test
    public void testGetAllFigmaDTOs_NullFigmaForProject_Success() {
        // Arrange
        Project project1 = new Project();
        project1.setProjectId(1L);
        project1.setProjectName("Project 1");
        project1.setFigma(null);

        Project project2 = new Project();
        project2.setProjectId(2L);
        project2.setProjectName("Project 2");
        project2.setFigma(null);

        List<Project> activeProjects = new ArrayList<>();
        activeProjects.add(project1);
        activeProjects.add(project2);

        when(projectRepository.findAllProjects()).thenReturn(activeProjects);

        // Act
        List<FigmaDTO> figmaDTOs = figmaService.getAllFigmaDTOs();

        // Assert
        assertEquals(0, figmaDTOs.size());
    }

    @Test
    public void testMapProjectToProjectDTO() {
        // Arrange
        Project project = new Project();
        project.setProjectId(1L);
        project.setProjectName("Test Project");

        // Act
        ProjectDTO projectDTO = figmaService.mapProjectToProjectDTO(project);

        // Assert
        assertEquals(project.getProjectId(), projectDTO.getProjectId());
        assertEquals(project.getProjectName(), projectDTO.getProjectName());
    }

    @Test
    public void testMapFigmaToFigmatDTO() {
        // Arrange
        Figma figma = new Figma();
        figma.setFigmaId(101L);
        figma.setFigmaURL("https://example.com/figma");

        // Act
        FigmaDTO figmaDTO = figmaService.mapFigmaToFigmatDTO(figma);

        // Assert
        assertEquals(figma.getFigmaId(), figmaDTO.getFigmaId());
        assertEquals(figma.getFigmaURL(), figmaDTO.getFigmaURL());
    }
    @Test
    public void testMapProjectDTOToProject() {
        // Arrange
        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setProjectId(1L);
        projectDTO.setProjectName("Test Project");

        // Act
        Project project = figmaService.mapProjectDTOToProject(projectDTO);

        // Assert
        assertEquals(projectDTO.getProjectId(), project.getProjectId());
        assertEquals(projectDTO.getProjectName(), project.getProjectName());
    }

    @Test
    public void testMapFigmaDTOToFigma() {
        // Arrange
        FigmaDTO figmaDTO = new FigmaDTO();
        figmaDTO.setFigmaId(101L);
        figmaDTO.setFigmaURL("https://example.com/figma");

        // Act
        Figma figma = figmaService.mapFigmaDTOToFigma(figmaDTO);

        // Assert
        assertEquals(figmaDTO.getFigmaId(), figma.getFigmaId());
        assertEquals(figmaDTO.getFigmaURL(), figma.getFigmaURL());
    }
}
