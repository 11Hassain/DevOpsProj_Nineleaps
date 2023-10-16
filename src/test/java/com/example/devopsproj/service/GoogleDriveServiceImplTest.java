package com.example.devopsproj.service;

import com.example.devopsproj.dto.responsedto.GoogleDriveDTO;
import com.example.devopsproj.dto.responsedto.ProjectDTO;
import com.example.devopsproj.model.GoogleDrive;
import com.example.devopsproj.model.Project;
import com.example.devopsproj.repository.GoogleDriveRepository;
import com.example.devopsproj.service.implementations.GoogleDriveServiceImpl;
import com.example.devopsproj.utils.DTOModelMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GoogleDriveServiceImplTest {

    @InjectMocks
    private GoogleDriveServiceImpl googleDriveService;
    @Mock
    private GoogleDriveRepository googleDriveRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    class CreateGoogleDriveTest {
        @Test
        @DisplayName("Testing success case for creating GDrive")
        void testCreateGoogleDrive_Success() {
            GoogleDriveDTO googleDriveDTO = new GoogleDriveDTO();
            googleDriveDTO.setDriveLink("https://drive.google.com/example");

            ProjectDTO projectDTO = new ProjectDTO();
            projectDTO.setProjectId(1L);
            projectDTO.setProjectName("Test Project");
            googleDriveDTO.setProjectDTO(projectDTO);

            GoogleDrive googleDriveToSave = new GoogleDrive();
            googleDriveToSave.setDriveLink(googleDriveDTO.getDriveLink());
            googleDriveToSave.setProject(DTOModelMapper.mapProjectDTOToProject(projectDTO));

            when(googleDriveRepository.save(any(GoogleDrive.class))).thenReturn(googleDriveToSave);

            GoogleDrive createdGoogleDrive = googleDriveService.createGoogleDrive(googleDriveDTO);

            assertNotNull(createdGoogleDrive);
            assertEquals(googleDriveDTO.getDriveLink(), createdGoogleDrive.getDriveLink());
            assertNotNull(createdGoogleDrive.getProject());
            assertEquals(projectDTO.getProjectId(), createdGoogleDrive.getProject().getProjectId());
            assertEquals(projectDTO.getProjectName(), createdGoogleDrive.getProject().getProjectName());
        }

        @Test
        @DisplayName("Testing failure case - Null DTO")
        void testCreateGoogleDrive_NullProjectDTO() {
            GoogleDriveDTO googleDriveDTO = new GoogleDriveDTO();
            googleDriveDTO.setDriveLink("https://drive.google.com/example");

            when(googleDriveRepository.save(any(GoogleDrive.class))).thenAnswer(invocation -> invocation.getArgument(0));

            GoogleDrive createdGoogleDrive = googleDriveService.createGoogleDrive(googleDriveDTO);

            assertNotNull(createdGoogleDrive);
            assertEquals(googleDriveDTO.getDriveLink(), createdGoogleDrive.getDriveLink());
            assertNull(createdGoogleDrive.getProject());
        }
    }

    @Nested
    class GetAllGoogleDrivesTest {
        @Test
        @DisplayName("Testing success case")
        void testGetAllGoogleDrives_Success() {
            GoogleDrive googleDrive1 = new GoogleDrive();
            googleDrive1.setDriveId(1L);
            googleDrive1.setDriveLink("https://drive.google.com/drive1");

            GoogleDrive googleDrive2 = new GoogleDrive();
            googleDrive2.setDriveId(2L);
            googleDrive2.setDriveLink("https://drive.google.com/drive2");

            GoogleDrive googleDrive3 = new GoogleDrive();
            googleDrive3.setDriveId(3L);
            googleDrive3.setDriveLink("https://drive.google.com/drive3");

            List<GoogleDrive> expectedGoogleDrives = Arrays.asList(
                    googleDrive1,
                    googleDrive2,
                    googleDrive3
            );

            when(googleDriveRepository.findAll()).thenReturn(expectedGoogleDrives);

            List<GoogleDrive> actualGoogleDrives = googleDriveService.getAllGoogleDrives();

            assertNotNull(actualGoogleDrives);
            assertEquals(expectedGoogleDrives.size(), actualGoogleDrives.size());
        }

        @Test
        @DisplayName("Testing failure case - empty list")
        void testGetAllGoogleDrives_EmptyList() {
            when(googleDriveRepository.findAll()).thenReturn(Collections.emptyList());

            List<GoogleDrive> actualGoogleDrives = googleDriveService.getAllGoogleDrives();

            assertNotNull(actualGoogleDrives);
            assertTrue(actualGoogleDrives.isEmpty());
        }
    }

    @Nested
    class GetGoogleDriveByIdTest {
        @Test
        @DisplayName("Testing success case - drive exists")
        void testGetGoogleDriveById_Exists() {
            Long driveId = 1L;

            GoogleDrive expectedGoogleDrive = new GoogleDrive();
            expectedGoogleDrive.setDriveId(1L);
            expectedGoogleDrive.setDriveLink("https://drive.google.com/drive1");

            when(googleDriveRepository.findById(driveId)).thenReturn(Optional.of(expectedGoogleDrive));

            Optional<GoogleDriveDTO> result = googleDriveService.getGoogleDriveById(driveId);

            assertTrue(result.isPresent());
            assertEquals(expectedGoogleDrive.getDriveLink(), result.get().getDriveLink());
        }

        @Test
        @DisplayName("Testing failure case - drive does not exist")
        void testGetGoogleDriveById_NotExists() {
            Long driveId = 1L;
            when(googleDriveRepository.findById(driveId)).thenReturn(Optional.empty());

            Optional<GoogleDriveDTO> result = googleDriveService.getGoogleDriveById(driveId);

            assertFalse(result.isPresent());
        }
    }

    @Nested
    class DeleteGoogleDriveByIdTest {
        @Test
        @DisplayName("Testing success case - drive exists")
        void testDeleteGoogleDriveById_Exists() {
            Long driveId = 1L;

            GoogleDrive expectedGoogleDrive = new GoogleDrive();
            expectedGoogleDrive.setDriveId(1L);
            expectedGoogleDrive.setDriveLink("https://drive.google.com/drive1");

            when(googleDriveRepository.findById(driveId)).thenReturn(Optional.of(expectedGoogleDrive));

            boolean result = googleDriveService.deleteGoogleDriveById(driveId);

            assertTrue(result);
            verify(googleDriveRepository, times(1)).deleteById(driveId);
        }

        @Test
        @DisplayName("Testing failure case - drive does not exist")
        void testDeleteGoogleDriveById_NotExists() {
            Long driveId = 1L;
            when(googleDriveRepository.findById(driveId)).thenReturn(Optional.empty());

            boolean result = googleDriveService.deleteGoogleDriveById(driveId);

            assertFalse(result);
            verify(googleDriveRepository, never()).deleteById(driveId);
        }
    }

    @Nested
    class GetGoogleDriveByProjectIdTest {
        @Test
        @DisplayName("Testing success case - drive exists")
        void testGetGoogleDriveByProjectId_Exists() {
            Long projectId = 1L;

            GoogleDrive expectedGoogleDrive = new GoogleDrive();
            expectedGoogleDrive.setDriveId(1L);
            expectedGoogleDrive.setDriveLink("https://drive.google.com/drive1");

            when(googleDriveRepository.findGoogleDriveByProjectId(projectId)).thenReturn(Optional.of(expectedGoogleDrive));

            Optional<GoogleDrive> result = googleDriveService.getGoogleDriveByProjectId(projectId);

            assertTrue(result.isPresent());
            assertEquals(expectedGoogleDrive, result.get());
        }

        @Test
        @DisplayName("Testing failure case - drive does not exist")
        void testGetGoogleDriveByProjectId_NotExists() {
            Long projectId = 1L;
            when(googleDriveRepository.findGoogleDriveByProjectId(projectId)).thenReturn(Optional.empty());

            Optional<GoogleDrive> result = googleDriveService.getGoogleDriveByProjectId(projectId);

            assertFalse(result.isPresent());
        }
    }

    @Test
    void testMapGoogleDriveToDTO() {
        Project project = new Project();
        project.setProjectId(1L);
        project.setProjectName("Test Project");

        GoogleDrive googleDrive = new GoogleDrive();
        googleDrive.setDriveId(1L);
        googleDrive.setDriveLink("https://drive.google.com/drive1");
        googleDrive.setProject(project);

        GoogleDriveDTO googleDriveDTO = googleDriveService.mapGoogleDriveToDTO(googleDrive);

        assertNotNull(googleDriveDTO);
        assertEquals(googleDrive.getDriveLink(), googleDriveDTO.getDriveLink());
        assertEquals(googleDrive.getDriveId(), googleDriveDTO.getDriveId());

        ProjectDTO projectDTO = googleDriveDTO.getProjectDTO();
        assertNotNull(projectDTO);
        assertEquals(project.getProjectId(), projectDTO.getProjectId());
        assertEquals(project.getProjectName(), projectDTO.getProjectName());
    }
}
