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
import com.example.devopsproj.service.interfaces.FigmaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.*;

 class FigmaServiceImplTest {

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
//   void testCreateFigma_Success() throws FigmaCreationException {
//        // Prepare input data
//        FigmaDTO figmaDTO = new FigmaDTO(new ProjectDTO(1L, "ProjectName"), "https://figma.com/project1");
//
//        // Mock project retrieval
//        Project project = new Project();
//        project.setProjectId(1L);
//        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
//
//        // Mock Figma creation and save
//        Figma savedFigma = new Figma();
//        when(figmaRepository.save(any(Figma.class))).thenReturn(savedFigma);
//
//        // Perform the Figma creation
//        Figma createdFigma = figmaService.createFigma(figmaDTO);
//
//        // Assert results
//        assertNotNull(createdFigma);
//        assertEquals(savedFigma, createdFigma);
//    }

     @Test
     void testCreateFigma_DataIntegrityViolationException() {
         FigmaDTO figmaDTO = new FigmaDTO(new ProjectDTO(1L, "ProjectName"), "https://figma.com/project1");

         Project project = new Project();
         project.setProjectId(1L);
         when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

         // Throw DataIntegrityViolationException when figmaRepository.save is called
         when(figmaRepository.save(any(Figma.class))).thenThrow(DataIntegrityViolationException.class);

         // Expect a FigmaCreationException, which should be caused by DataIntegrityViolationException
         assertThrows(FigmaCreationException.class, () -> figmaService.createFigma(figmaDTO));
     }

     @Test
     void testCreateFigma_GeneralException() {
         FigmaDTO figmaDTO = new FigmaDTO(new ProjectDTO(1L, "ProjectName"), "https://figma.com/project1");

         Project project = new Project();
         project.setProjectId(1L);
         when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

         // Throw a general Exception when figmaRepository.save is called
         when(figmaRepository.save(any(Figma.class))).thenThrow(new RuntimeException("Some error"));

         // Expect a FigmaCreationException, which should be caused by a general Exception
         assertThrows(FigmaCreationException.class, () -> figmaService.createFigma(figmaDTO));
     }

     @Test
     void testCreateFigma() {
         FigmaDTO figmaDTO = new FigmaDTO(new ProjectDTO(1L, "ProjectName"), "https://figma.com/project1");

         Project project = new Project();
         project.setProjectId(1L);
         when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

         // Ensure figmaRepository.save returns a valid Figma object (no exception)
         when(figmaRepository.save(any(Figma.class))).thenAnswer(invocation -> invocation.getArgument(0));

         // Expect no exception when createFigma is called
         Figma createdFigma = figmaService.createFigma(figmaDTO);

         // Assert that the createdFigma is not null
         assertNotNull(createdFigma);
     }

    @Test
    void testGetAllFigmaProjects_Successs() {
        // Prepare mock data
        Project project1 = new Project();
        project1.setProjectId(1L);
        Project project2 = new Project();
        project2.setProjectId(2L);

        when(projectRepository.findAllProjects()).thenReturn(List.of(project1, project2));

        // Perform the method
        List<Figma> figmaProjects = figmaService.getAllFigmaProjects();

        // Verify results
        assertNotNull(figmaProjects);
        assertEquals(2, figmaProjects.size());
    }

     @Test
     void testGetAllFigmaProjects_Exception() {
         // Mock an exception when calling projectRepository.findAllProjects
         when(projectRepository.findAllProjects()).thenThrow(new RuntimeException("Simulated Exception"));

         // Perform the method and catch the exception
         Exception exception = assertThrows(RuntimeException.class, () -> {
             figmaService.getAllFigmaProjects();
         });

         // Verify the exception message
         assertTrue(exception.getMessage().contains("Simulated Exception"));
     }


     @Test
     void testCreateFigma_Exception() {
         // Arrange
         FigmaDTO figmaDTO = new FigmaDTO();
         // Set up figmaDTO with necessary data

         when(figmaRepository.save(Mockito.any(Figma.class)))
                 .thenThrow(new RuntimeException("Some unexpected exception"));

         // Act and Assert
         assertThrows(RuntimeException.class, () -> figmaService.createFigma(figmaDTO));
     }


//     @Test
//     void testCreateFigma_DataIntegrityViolationException() {
//         FigmaDTO figmaDTO = new FigmaDTO(new ProjectDTO(1L, "ProjectName"), "https://figma.com/project1");
//
//         Project project = new Project();
//         project.setProjectId(1L);
//         when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
//
//         // Configure figmaRepository to throw a DataIntegrityViolationException
//         doThrow(DataIntegrityViolationException.class).when(figmaRepository).save(any(Figma.class));
//
//         // Expect DataIntegrityViolationException when createFigma is called
//         assertThrows(DataIntegrityViolationException.class, () -> figmaService.createFigma(figmaDTO));
//     }






     @Test
    void testGetAllFigmaProjects_Success() {
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
        when(figmaRepository.findFigmaByProjectId((project1.getProjectId()))).thenReturn(Optional.of(figma1));
        when(figmaRepository.findFigmaByProjectId((project2.getProjectId()))).thenReturn(Optional.of(figma2));

        // Act
        List<Figma> figmaProjects = figmaService.getAllFigmaProjects();

        // Assert
        assertNotNull(figmaProjects);
        assertEquals(2, figmaProjects.size());
        // Add additional assertions as needed
    }

    @Test
    void testGetFigmaById_Found() {
        Long figmaId = 1L;
        Figma figma = new Figma();
        Project project = new Project();
        project.setProjectId(1L);
        project.setProjectName("Test Project");
        figma.setProject(project);
        figma.setFigmaURL("https://example.com/figma");

        when(figmaRepository.findById(figmaId)).thenReturn(Optional.of(figma));

        Optional<FigmaDTO> figmaDTOOptional = figmaService.getFigmaById(figmaId);

        assertTrue(figmaDTOOptional.isPresent());
        FigmaDTO figmaDTO = figmaDTOOptional.get();
        assertNotNull(figmaDTO.getProjectDTO());
        assertEquals(1L, figmaDTO.getProjectDTO().getProjectId());
        assertEquals("Test Project", figmaDTO.getProjectDTO().getProjectName());
        assertEquals("https://example.com/figma", figmaDTO.getFigmaURL());
    }
    @Test
    void testAddUserAndScreenshots_FigmaFound() {
        Long figmaId = 1L;
        String user = "User1";
        String screenshotImage = "screenshot1.png";
        FigmaDTO figmaDTO = new FigmaDTO();
        figmaDTO.setUser(user);
        figmaDTO.setScreenshotImage(screenshotImage);

        Figma figma = new Figma();
        when(figmaRepository.findById(figmaId)).thenReturn(Optional.of(figma));

        figmaService.addUserAndScreenshots(figmaId, figmaDTO);

        // Verify that the Figma object has been updated with the new user and screenshot.
        Map<String, String> screenshotImagesByUser = figma.getScreenshotImagesByUser();
        assertNotNull(screenshotImagesByUser);
        assertTrue(screenshotImagesByUser.containsKey(user));
        assertEquals(screenshotImage, screenshotImagesByUser.get(user));

        // Ensure that figmaRepository.save has been called once.
        verify(figmaRepository, times(1)).save(figma);
    }

    @Test
    void testAddUserAndScreenshots_FigmaFoundd() {
        Long figmaId = 1L;
        String user = "User1";
        String screenshotImage = "screenshot1.png";
        FigmaDTO figmaDTO = new FigmaDTO();
        figmaDTO.setUser(user);
        figmaDTO.setScreenshotImage(screenshotImage);

        Figma figma = new Figma();
        when(figmaRepository.findById(figmaId)).thenReturn(Optional.of(figma));

        figmaService.addUserAndScreenshots(figmaId, figmaDTO);

        // Verify that the Figma object has been updated with the new user and screenshot.
        Map<String, String> screenshotImagesByUser = figma.getScreenshotImagesByUser();
        assertNotNull(screenshotImagesByUser);
        assertTrue(screenshotImagesByUser.containsKey(user));
        assertEquals(screenshotImage, screenshotImagesByUser.get(user));

        // Ensure that figmaRepository.save has been called once.
        verify(figmaRepository, times(1)).save(figma);

        // Ensure that screenshotImagesByUser is initialized with an empty HashMap
        assertEquals(1, screenshotImagesByUser.size()); // The size should be 1 (user -> screenshot)
    }
    @Test
    void testAddUserAndScreenshots_InitializeScreenshotImagesByUser() {
        Long figmaId = 1L;
        String user = "User1";
        String screenshotImage = "screenshot1.png";
        FigmaDTO figmaDTO = new FigmaDTO();
        figmaDTO.setUser(user);
        figmaDTO.setScreenshotImage(screenshotImage);

        Figma figma = new Figma();
        // Simulate a scenario where screenshotImagesByUser is initially null in Figma.
        figma.setScreenshotImagesByUser(null);

        when(figmaRepository.findById(figmaId)).thenReturn(Optional.of(figma));

        figmaService.addUserAndScreenshots(figmaId, figmaDTO);

        // Verify that the Figma object has been updated with the new user and screenshot.
        Map<String, String> screenshotImagesByUser = figma.getScreenshotImagesByUser();
        assertNotNull(screenshotImagesByUser);
        assertTrue(screenshotImagesByUser.containsKey(user));
        assertEquals(screenshotImage, screenshotImagesByUser.get(user));

        // Ensure that figmaRepository.save has been called once.
        verify(figmaRepository, times(1)).save(figma);

        // Ensure that screenshotImagesByUser is initialized with an empty HashMap
        assertEquals(1, screenshotImagesByUser.size()); // The size should be 1 (user -> screenshot)
    }

    @Test
    void testAddUserAndScreenshots_FigmaNotFound() {
        Long figmaId = 1L;
        String user = "User1";
        String screenshotImage = "screenshot1.png";
        FigmaDTO figmaDTO = new FigmaDTO();
        figmaDTO.setUser(user);
        figmaDTO.setScreenshotImage(screenshotImage);

        when(figmaRepository.findById(figmaId)).thenReturn(Optional.empty());

        // Verify that a FigmaNotFoundException is thrown when Figma is not found.
        assertThrows(FigmaNotFoundException.class, () -> figmaService.addUserAndScreenshots(figmaId, figmaDTO));
    }


    @Test
    void testGetAllFigmaProjects_EmptyList() {
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
    void testGetFigmaById_FigmaNotFound() {
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
     void testSoftDeleteFigma_Success() {
         // Arrange
         Long figmaId = 1L;
         Figma figma = new Figma();
         figma.setFigmaId(figmaId);

         when(figmaRepository.findById(figmaId)).thenReturn(Optional.of(figma));

         // Act
         assertDoesNotThrow(() -> figmaService.softDeleteFigma(figmaId));

         // Assert
         assertTrue(figma.deleted);
         verify(figmaRepository, times(1)).save(figma);
         verify(figmaRepository, times(1)).findById(figmaId);
     }

     @Test
     void testSoftDeleteFigma_FigmaNotFound() {
         // Arrange
         Long figmaId = 1L;
         when(figmaRepository.findById(figmaId)).thenReturn(Optional.empty());

         // Act and Assert
         FigmaNotFoundException exception = assertThrows(FigmaNotFoundException.class, () -> figmaService.softDeleteFigma(figmaId));
         assertEquals("Figma with ID 1 not found", exception.getMessage());
     }






    @Test
    void testGetFigmaURLByProjectId_FigmaFound() {
        // Arrange
        Long projectId = 1L;
        String figmaURL = "https://example.com/figma";
        Figma figma = new Figma(); // Create a new Figma object
        figma.setFigmaURL(figmaURL); // Set the figmaURL using the setter method

        // Mock the figmaRepository.findFigmaByProjectId method to return a Figma object
        when(figmaRepository.findFigmaByProjectId((projectId))).thenReturn(Optional.of(figma));

        // Act
        String result = figmaService.getFigmaURLByProjectId(projectId);

        // Assert
        assertNotNull(result);
        assertEquals(figmaURL, result);
    }

    @Test
    void testGetFigmaURLByProjectId_FigmaNotFound() {
        // Arrange
        Long projectId = 1L;

        // Mock the figmaRepository.findFigmaByProjectId method to return an empty Optional
        when(figmaRepository.findFigmaByProjectId((projectId))).thenReturn(Optional.empty());

        // Act
        String result = figmaService.getFigmaURLByProjectId(projectId);

        // Assert
        assertNull(result);
    }


    @Test
    void testGetScreenshotsForFigmaIdWithScreenshots_Success() {
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
    void testGetScreenshotsForFigmaIdWithNoScreenshots_Success() {
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
    void testGetScreenshotsForFigmaIdWithEmptyScreenshots_Success() {
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
    void testGetScreenshotsForFigmaIdWithFigmaNotFound_Success() {
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
    void testGetAllFigmaDTOs_Success() {
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
    void testGetAllFigmaDTOs_NoActiveProjects_Success() {
        // Arrange
        when(projectRepository.findAllProjects()).thenReturn(new ArrayList<>());

        // Act
        List<FigmaDTO> figmaDTOs = figmaService.getAllFigmaDTOs();

        // Assert
        assertEquals(0, figmaDTOs.size());
    }

    @Test
    void testGetAllFigmaDTOs_NullFigmaForProject_Success() {
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
    void testMapProjectToProjectDTO() {
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
    void testMapFigmaToFigmatDTO() {
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
    void testMapProjectDTOToProject() {
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
    void testMapFigmaDTOToFigma() {
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
    @Test
    void testGetAllFigmaDTOs() {
            Project project1 = new Project();
            Figma figma1 = new Figma();
            figma1.setFigmaURL("https://example.com/figma1");
            project1.setFigma(figma1);

            Project project2 = new Project();
            Figma figma2 = new Figma();
            figma2.setFigmaURL("https://example.com/figma2");
            project2.setFigma(figma2);

            List<Project> activeProjects = Arrays.asList(project1, project2);

            when(projectRepository.findAllProjects()).thenReturn(activeProjects);

            List<Figma> figmaProjects = figmaService.getAllFigmaProjects();

            assertNotNull(figmaProjects);
            assertEquals(2, figmaProjects.size());

            assertEquals("https://example.com/figma1", figmaProjects.get(0).getFigmaURL());
            assertEquals("https://example.com/figma2", figmaProjects.get(1).getFigmaURL());
        }
    @Test
    void testGetAllFigmaDTOs_WithFigma() {
        // Create a project with an associated Figma object
        Project project = new Project();
        Figma figma = new Figma();
        figma.setFigmaId(1L);
        figma.setProject(project);
        project.setFigma(figma);

        // Mock the repository to return the project with Figma
        List<Project> activeProjects = Collections.singletonList(project);
        when(projectRepository.findAllProjects()).thenReturn(activeProjects);

        // Call the method under test
        List<FigmaDTO> figmaDTOs = figmaService.getAllFigmaDTOs();

        // Assertions
        assertEquals(1, figmaDTOs.size()); // Check that one FigmaDTO is added
        FigmaDTO figmaDTO = figmaDTOs.get(0);
        assertEquals(figma.getFigmaId(), figmaDTO.getFigmaId());
        // You can add more assertions for other fields if needed
    }

}
