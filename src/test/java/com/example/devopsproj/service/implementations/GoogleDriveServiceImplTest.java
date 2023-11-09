package com.example.devopsproj.service.implementations;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

import com.example.devopsproj.dto.responsedto.GoogleDriveDTO;
import com.example.devopsproj.dto.responsedto.ProjectDTO;
import com.example.devopsproj.exceptions.NotFoundException;
import com.example.devopsproj.model.GoogleDrive;
import com.example.devopsproj.model.Project;
import com.example.devopsproj.repository.GoogleDriveRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import static org.mockito.Mockito.*;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

 class GoogleDriveServiceImplTest {

    @Mock
    private GoogleDriveRepository googleDriveRepository;

    @InjectMocks
    private GoogleDriveServiceImpl googleDriveService;

    @BeforeEach
     void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
     void testCreateGoogleDrive_Success() {
        // Arrange
        GoogleDriveDTO googleDriveDTO = new GoogleDriveDTO();
        googleDriveDTO.setDriveLink("https://example.com/drive");

        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setProjectId(1L);
        projectDTO.setProjectName("Test Project");

        googleDriveDTO.setProjectDTO(projectDTO);

        GoogleDrive googleDrive = new GoogleDrive();
        googleDrive.setDriveLink(googleDriveDTO.getDriveLink());
        googleDrive.setDriveId(101L);
        googleDrive.setProject(mapProjectDTOToProject(projectDTO));

        when(googleDriveRepository.save(any(GoogleDrive.class))).thenReturn(googleDrive);

        // Act
        GoogleDriveDTO createdGoogleDrive = googleDriveService.createGoogleDrive(googleDriveDTO);

        // Assert
        assertEquals(googleDriveDTO.getDriveLink(), createdGoogleDrive.getDriveLink());
        assertEquals(googleDrive.getDriveId(), createdGoogleDrive.getDriveId());
        assertEquals(projectDTO.getProjectName(), createdGoogleDrive.getProjectDTO().getProjectName());
    }

    private Project mapProjectDTOToProject(ProjectDTO projectDTO) {
        // Implement the mapping logic here or use a mapper like ModelMapper
        Project project = new Project();
        project.setProjectId(projectDTO.getProjectId());
        project.setProjectName(projectDTO.getProjectName());
        return project;
    }

//    @Test
//     void testGetAllGoogleDrives_Success() {
//        // Arrange
//        Project project1 = new Project();
//        project1.setProjectId(1L);
//        project1.setProjectName("Project 1");
//
//        Project project2 = new Project();
//        project2.setProjectId(2L);
//        project2.setProjectName("Project 2");
//
//        List<GoogleDrive> googleDrives = new ArrayList<>();
//        googleDrives.add(createGoogleDrive(101L, "https://example.com/drive1", project1));
//        googleDrives.add(createGoogleDrive(102L, "https://example.com/drive2", project2));
//
//        when(googleDriveRepository.findAll()).thenReturn(googleDrives);
//
//        // Act
//        List<GoogleDriveDTO> googleDriveDTOs = googleDriveService.getAllGoogleDrives();
//
//        // Assert
//        assertEquals(2, googleDriveDTOs.size());
//        assertEquals("Project 1", googleDriveDTOs.get(0).getProjectDTO().getProjectName());
//        assertEquals("Project 2", googleDriveDTOs.get(1).getProjectDTO().getProjectName());
//    }

    private GoogleDrive createGoogleDrive(Long driveId, String driveLink, Project project) {
        GoogleDrive googleDrive = new GoogleDrive();
        googleDrive.setDriveId(driveId);
        googleDrive.setDriveLink(driveLink);
        googleDrive.setProject(project);
        return googleDrive;
    }

    @Test
     void testGetGoogleDriveById_Success() {
        // Arrange
        Project project = new Project();
        project.setProjectId(1L);
        project.setProjectName("Project 1");

        Long driveId = 101L;
        String driveLink = "https://example.com/drive1";

        GoogleDrive googleDrive = new GoogleDrive();
        googleDrive.setDriveId(driveId);
        googleDrive.setDriveLink(driveLink);
        googleDrive.setProject(project);

        when(googleDriveRepository.findById(driveId)).thenReturn(Optional.of(googleDrive));

        // Act
        Optional<GoogleDriveDTO> optionalGoogleDriveDTO = googleDriveService.getGoogleDriveById(driveId);

        // Assert
        assertTrue(optionalGoogleDriveDTO.isPresent());
        GoogleDriveDTO googleDriveDTO = optionalGoogleDriveDTO.get();
        assertEquals(driveId, googleDriveDTO.getDriveId());
        assertEquals(driveLink, googleDriveDTO.getDriveLink());
        assertEquals("Project 1", googleDriveDTO.getProjectDTO().getProjectName());
    }

    @Test
     void testGetGoogleDriveById_NotFound() {
        // Arrange
        Long driveId = 101L;

        when(googleDriveRepository.findById(driveId)).thenReturn(Optional.empty());

        // Act
        Optional<GoogleDriveDTO> optionalGoogleDriveDTO = googleDriveService.getGoogleDriveById(driveId);

        // Assert
        assertFalse(optionalGoogleDriveDTO.isPresent());
    }


    @Test
    void testDeleteGoogleDriveById_Success() {
       // Arrange
       Long driveId = 1L;

       // Mock the googleDriveRepository.findById method to return a GoogleDrive object
       GoogleDrive googleDrive = new GoogleDrive();
       when(googleDriveRepository.findById(driveId)).thenReturn(Optional.of(googleDrive));

       // Act
       ResponseEntity<String> response = googleDriveService.deleteGoogleDriveById(driveId);

       // Assert
       assertEquals("Google Drive with ID: 1 has been soft-deleted successfully.", response.getBody());
       assertEquals(200, response.getStatusCodeValue());

       // Verify that findById method was called
       Mockito.verify(googleDriveRepository, times(1)).findById(driveId);
    }




    @Test
     void testDeleteGoogleDriveById_NotFound() {
        // Arrange
        Long driveId = 1L;

        // Mock the googleDriveRepository.findById method to return an empty Optional
        when(googleDriveRepository.findById(driveId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<String> response = googleDriveService.deleteGoogleDriveById(driveId);

        // Assert
        assertEquals(404, response.getStatusCodeValue());
        // Ensure that findById was called
        Mockito.verify(googleDriveRepository, times(1)).findById(driveId);
    }


    @Test
     void testGetGoogleDriveByProjectId_NotFound() {
        // Arrange
        Long projectId = 1L;

        // Mock the googleDriveRepository.findGoogleDriveByProjectId method to return an empty Optional
        when(googleDriveRepository.findGoogleDriveByProjectId(projectId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<GoogleDriveDTO> response = googleDriveService.getGoogleDriveByProjectId(projectId);

        // Assert
        assertEquals(404, response.getStatusCodeValue());

        // Verify that findGoogleDriveByProjectId was called
        Mockito.verify(googleDriveRepository, Mockito.times(1)).findGoogleDriveByProjectId(projectId);
    }

    @Test
     void testCreateGoogleDrive_Successs() {
        // Arrange
        GoogleDriveDTO googleDriveDTO = new GoogleDriveDTO();
        googleDriveDTO.setDriveLink("https://example.com/drive");

        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setProjectId(1L);
        projectDTO.setProjectName("Test Project");

        googleDriveDTO.setProjectDTO(projectDTO);

        GoogleDrive googleDrive = new GoogleDrive();
        googleDrive.setDriveLink(googleDriveDTO.getDriveLink());
        googleDrive.setDriveId(101L);
        googleDrive.setProject(mapProjectDTOToProject(projectDTO));

        when(googleDriveRepository.save(any(GoogleDrive.class))).thenReturn(googleDrive);

        // Act
        GoogleDriveDTO createdGoogleDrive = googleDriveService.createGoogleDrive(googleDriveDTO);

        // Assert
        assertEquals(googleDriveDTO.getDriveLink(), createdGoogleDrive.getDriveLink());
        assertEquals(googleDrive.getDriveId(), createdGoogleDrive.getDriveId());
        assertEquals(projectDTO.getProjectName(), createdGoogleDrive.getProjectDTO().getProjectName());
    }
    @Test
     void testMapProjectToProjectDTO() {
        // Arrange
        Project project = new Project();
        project.setProjectId(1L);
        project.setProjectName("Test Project");

        // Act
        ProjectDTO projectDTO = googleDriveService.mapProjectToProjectDTO(project);

        // Assert
        assertEquals(project.getProjectId(), projectDTO.getProjectId());
        assertEquals(project.getProjectName(), projectDTO.getProjectName());
    }

    @Test
     void testMapProjectDTOToProject() {
        // Arrange
        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setProjectId(1L);
        projectDTO.setProjectName("Test Project");

        // Act
        Project project = googleDriveService.mapProjectDTOToProject(projectDTO);

        // Assert
        assertEquals(projectDTO.getProjectId(), project.getProjectId());
        assertEquals(projectDTO.getProjectName(), project.getProjectName());
    }

    @Test
     void testGetGoogleDriveByProjectIdNotFound() {
        // Mock data
        long projectId = 1L;

        // Mock the repository to return empty Optional
        when(googleDriveRepository.findGoogleDriveByProjectId(projectId)).thenReturn(Optional.empty());

        // Test the service method
        ResponseEntity<GoogleDriveDTO> responseEntity = googleDriveService.getGoogleDriveByProjectId(projectId);

        // Verify the response
        assertEquals(404, responseEntity.getStatusCodeValue());
    }

    @Test
     void testMapToGoogleDriveDTO() {
        // Mock data
        long projectId = 1L;
        String projectName = "ProjectName";
        String driveLink = "DriveLink";
        long driveId = 2L;
        GoogleDrive googleDrive = new GoogleDrive();
        googleDrive.setProject(googleDriveService.mapProjectDTOToProject(new ProjectDTO(projectId, projectName)));
        googleDrive.setDriveLink(driveLink);
        googleDrive.setDriveId(driveId);

        // Test the mapping method
        ResponseEntity<GoogleDriveDTO> responseEntity = googleDriveService.mapToGoogleDriveDTO(googleDrive);

        // Verify the response
        assertEquals(200, responseEntity.getStatusCodeValue());
        GoogleDriveDTO googleDriveDTO = responseEntity.getBody();
        assertEquals(projectId, googleDriveDTO.getProjectDTO().getProjectId());
        assertEquals(projectName, googleDriveDTO.getProjectDTO().getProjectName());
        assertEquals(driveLink, googleDriveDTO.getDriveLink());
        assertEquals(driveId, googleDriveDTO.getDriveId());
    }





    @Test
    void testGetAllGoogleDrives() {
       // Create a sample GoogleDrive object
       GoogleDrive googleDrive = new GoogleDrive();
       googleDrive.setDriveLink("https://example.com/drive");
       Long driveId = 12345L; // Use Long.valueOf to create a Long
       googleDrive.setDriveId(driveId);
       googleDrive.setProject(new Project()); // You may need to set a project here

       // Create a sample Page of GoogleDrive
       Page<GoogleDrive> googleDrivePage = new PageImpl<>(Collections.singletonList(googleDrive));

       // Mock the googleDriveRepository to return the sample Page when findAll is called
       when(googleDriveRepository.findAll(any(Pageable.class))).thenReturn(googleDrivePage);

       // Call the getAllGoogleDrives method
       Page<GoogleDriveDTO> result = googleDriveService.getAllGoogleDrives(PageRequest.of(0, 10));

       // Verify that the result is not empty
       assertFalse(result.isEmpty());

       // Verify that the mapping was done correctly
       GoogleDriveDTO googleDriveDTO = result.getContent().get(0);
       assertEquals("https://example.com/drive", googleDriveDTO.getDriveLink());
       assertEquals(driveId, googleDriveDTO.getDriveId()); // Compare with the Long value
       // Add more assertions if needed for other properties.
    }


    @Test
    void testGetAllGoogleDrives_NoEntriesFound() {
       // Create an empty Page of GoogleDrive
       Page<GoogleDrive> emptyPage = new PageImpl<>(Collections.emptyList());

       // Mock the googleDriveRepository to return the empty Page when findAll is called
       when(googleDriveRepository.findAll(any(Pageable.class))).thenReturn(emptyPage);

       // Call the getAllGoogleDrives method and expect a NotFoundException
       assertThrows(NotFoundException.class, () -> googleDriveService.getAllGoogleDrives(PageRequest.of(0, 10)));
    }

    @Test
    void testMapGoogleDriveToGoogleDriveDTO() {
       // Create a sample GoogleDrive object
       GoogleDrive googleDrive = new GoogleDrive();
       googleDrive.setDriveLink("https://example.com/drive");
       Long driveId = 12345L; // Use Long.valueOf to create a Long
       googleDrive.setDriveId(driveId);
       googleDrive.setProject(new Project()); // You may need to set a project here

       // Call the mapGoogleDriveToGoogleDriveDTO method
       GoogleDriveDTO googleDriveDTO = googleDriveService.mapGoogleDriveToGoogleDriveDTO(googleDrive);

       // Verify that the mapping is correct
       assertEquals("https://example.com/drive", googleDriveDTO.getDriveLink());
       assertEquals(driveId, googleDriveDTO.getDriveId()); // Compare with the Long value
    }



 }
