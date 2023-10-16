package com.example.devopsproj.service;

import com.example.devopsproj.dto.responsedto.FigmaDTO;
import com.example.devopsproj.dto.responsedto.FigmaScreenshotDTO;
import com.example.devopsproj.dto.responsedto.ProjectDTO;
import com.example.devopsproj.exceptions.NotFoundException;
import com.example.devopsproj.model.Figma;
import com.example.devopsproj.model.Project;
import com.example.devopsproj.repository.FigmaRepository;
import com.example.devopsproj.repository.ProjectRepository;
import com.example.devopsproj.service.implementations.FigmaServiceImpl;
import com.example.devopsproj.utils.DTOModelMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FigmaServiceImplTest {

    @InjectMocks
    private FigmaServiceImpl figmaService;
    @Mock
    private FigmaRepository figmaRepository;
    @Mock
    private ProjectRepository projectRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateFigma_Success() {
        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setProjectId(1L);
        projectDTO.setProjectName("Test Project");

        FigmaDTO figmaDTO = new FigmaDTO();
        figmaDTO.setProjectDTO(projectDTO);
        figmaDTO.setFigmaURL("https://example.com/figma");

        Figma figmaToSave = new Figma();
        figmaToSave.setProject(DTOModelMapper.mapProjectDTOToProject(projectDTO));
        figmaToSave.setFigmaURL(figmaDTO.getFigmaURL());

        when(figmaRepository.save(any(Figma.class))).thenReturn(figmaToSave);

        figmaService.createFigma(figmaDTO);

        verify(figmaRepository, times(1)).save(argThat(savedFigma -> {
            assertEquals(figmaToSave.getFigmaURL(), savedFigma.getFigmaURL());
            assertNotNull(savedFigma.getProject());
            assertEquals(figmaToSave.getProject().getProjectId(), savedFigma.getProject().getProjectId());
            assertEquals(figmaToSave.getProject().getProjectName(), savedFigma.getProject().getProjectName());
            return true;
        }));
    }

    @Nested
    class GetAllFigmaProjectsTest {
        @Test
        @DisplayName("Testing success case for getting all figma projects")
        void testGetAllFigmaProjects_Success() {
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
        @DisplayName("Testing no projects case")
        void testGetAllFigmaProjects_NoProjects() {
            when(projectRepository.findAllProjects()).thenReturn(Collections.emptyList());

            List<Figma> figmaProjects = figmaService.getAllFigmaProjects();

            assertNotNull(figmaProjects);
            assertTrue(figmaProjects.isEmpty());
        }
    }

    @Nested
    class GetFigmaByIdTest {
        @Test
        @DisplayName("Testing success case for figma (found)")
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
        @DisplayName("Testing failure case for figma (not found)")
        void testGetFigmaById_NotFound() {
            Long figmaId = 1L;
            when(figmaRepository.findById(figmaId)).thenReturn(Optional.empty());

            Optional<FigmaDTO> figmaDTOOptional = figmaService.getFigmaById(figmaId);

            assertFalse(figmaDTOOptional.isPresent());
        }
    }

    @Test
    void testDeleteFigma_Success() {
        Long figmaId = 1L;

        figmaService.deleteFigma(figmaId);

        verify(figmaRepository, times(1)).deleteById(figmaId);
    }

    @Nested
    class SaveUserAndScreenshotsToFigmaTest {
        @Test
        @DisplayName("Testing success case for saving")
        void testSaveUserAndScreenshotsToFigma_Success() {
            Long figmaId = 1L;
            FigmaDTO figmaDTO = new FigmaDTO();
            figmaDTO.setUser("John Doe");
            figmaDTO.setScreenshotImage("screenshot1.png");

            Figma figma = new Figma();
            figma.setFigmaId(figmaId);
            when(figmaRepository.findById(figmaId)).thenReturn(Optional.of(figma));

            String result = figmaService.saveUserAndScreenshotsToFigma(figmaId, figmaDTO);

            assertEquals("User and screenshot added", result);

            Map<String, String> screenshotImagesByUser = figma.getScreenshotImagesByUser();
            assertNotNull(screenshotImagesByUser);
            assertTrue(screenshotImagesByUser.containsKey("John Doe"));
            assertEquals("screenshot1.png", screenshotImagesByUser.get("John Doe"));

            verify(figmaRepository, times(1)).save(figma);
        }

        @Test
        @DisplayName("Testing failure case - figma not found")
        void testSaveUserAndScreenshotsToFigma_FigmaNotFound() {
            Long figmaId = 1L;
            FigmaDTO figmaDTO = new FigmaDTO();
            figmaDTO.setUser("John Doe");
            figmaDTO.setScreenshotImage("screenshot1.png");

            when(figmaRepository.findById(figmaId)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> figmaService.saveUserAndScreenshotsToFigma(figmaId, figmaDTO));
        }
    }

    @Nested
    class GetFigmaURLByProjectIdTest {
        @Test
        @DisplayName("Testing success case - found")
        void testGetFigmaURLByProjectId_Found() {
            Long projectId = 1L;
            Figma figma = new Figma();
            figma.setFigmaURL("https://example.com/figma");
            when(figmaRepository.findFigmaByProjectId(projectId)).thenReturn(Optional.of(figma));

            String result = figmaService.getFigmaURLByProjectId(projectId);

            assertEquals("https://example.com/figma", result);
        }

        @Test
        @DisplayName("Testing failure case - not found")
        void testGetFigmaURLByProjectId_NotFound() {
            Long projectId = 1L;
            when(figmaRepository.findFigmaByProjectId(projectId)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> figmaService.getFigmaURLByProjectId(projectId));
        }
    }

    @Nested
    class GetScreenshotsByFigmaIdTest {
        @Test
        @DisplayName("Testing success case - found")
        void testGetScreenshotsByFigmaId_FoundScreenshots() {
            Long figmaId = 1L;
            Figma figma = new Figma();
            Map<String, String> screenshotImagesByUser = new HashMap<>();
            screenshotImagesByUser.put("John Doe", "screenshot1.png");
            screenshotImagesByUser.put("Jane Smith", "screenshot2.png");
            figma.setScreenshotImagesByUser(screenshotImagesByUser);

            when(figmaRepository.findById(figmaId)).thenReturn(Optional.of(figma));

            List<FigmaScreenshotDTO> result = figmaService.getScreenshotsByFigmaId(figmaId);

            assertNotNull(result);
            assertEquals(2, result.size());

            FigmaScreenshotDTO dto1 = result.get(0);
            assertEquals("John Doe", dto1.getUser());
            assertEquals("screenshot1.png", dto1.getScreenshotImageURL());

            FigmaScreenshotDTO dto2 = result.get(1);
            assertEquals("Jane Smith", dto2.getUser());
            assertEquals("screenshot2.png", dto2.getScreenshotImageURL());
        }

        @Test
        @DisplayName("Testing failure case - no screenshots")
        void testGetScreenshotsByFigmaId_NoScreenshots() {
            Long figmaId = 1L;
            Figma figma = new Figma();
            when(figmaRepository.findById(figmaId)).thenReturn(Optional.of(figma));

            assertThrows(NotFoundException.class, () -> figmaService.getScreenshotsByFigmaId(figmaId));
        }

        @Test
        @DisplayName("Testing failure case - figma not found")
        void testGetScreenshotsByFigmaId_FigmaNotFound() {
            Long figmaId = 1L;
            when(figmaRepository.findById(figmaId)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> figmaService.getScreenshotsByFigmaId(figmaId));
        }
    }
}
