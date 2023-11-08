package com.example.devopsproj.controller;

import com.example.devopsproj.dto.responsedto.GoogleDriveDTO;
import com.example.devopsproj.service.interfaces.GoogleDriveService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

 class GoogleDriveControllerTest {

    @InjectMocks
    private GoogleDriveController googleDriveController;

    @Mock
    private GoogleDriveService googleDriveService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
     void testCreateGoogleDrive_Success() {
        // Prepare mock data
        GoogleDriveDTO inputDTO = new GoogleDriveDTO();
        inputDTO.setDriveId(1L);
        inputDTO.setDriveLink("My Drive");

        // Mock the service method to return the created GoogleDriveDTO
        when(googleDriveService.createGoogleDrive(inputDTO)).thenReturn(inputDTO);

        // Call the controller method
        ResponseEntity<GoogleDriveDTO> response = googleDriveController.createGoogleDrive(inputDTO);

        // Verify the HTTP status code
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        // Verify the response body
        GoogleDriveDTO responseBody = response.getBody();
        assertEquals(inputDTO.getDriveId(), responseBody.getDriveId());
        assertEquals(inputDTO.getDriveLink(), responseBody.getDriveLink());
        // Add more assertions for other properties if necessary
    }
//    @Test
//    void testGetAllGoogleDrives_Success() {
//        // Prepare mock data
//        List<GoogleDriveDTO> googleDriveDTOs = new ArrayList<>();
//        googleDriveDTOs.add(new GoogleDriveDTO("Drive 1", 1L));
//        googleDriveDTOs.add(new GoogleDriveDTO("Drive 2", 2L));
//
//        // Mock the service method to return the mock data
//        when(googleDriveService.getAllGoogleDrives()).thenReturn(googleDriveDTOs);
//
//        // Call the controller method
//        ResponseEntity<List<GoogleDriveDTO>> response = googleDriveController.getAllGoogleDrives();
//
//        // Verify the HTTP status code
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//
//        // Verify the response body
//        List<GoogleDriveDTO> responseBody = response.getBody();
//        assertEquals(googleDriveDTOs.size(), responseBody.size());
//        // Add more assertions to compare individual DTO properties if needed
//    }

    @Test
     void testGetGoogleDriveById_Exists() {
        // Prepare mock data
        Long driveId = 1L;
        GoogleDriveDTO googleDriveDTO = new GoogleDriveDTO("Drive 1", driveId);
        Optional<GoogleDriveDTO> optionalGoogleDriveDTO = Optional.of(googleDriveDTO);

        // Mock the service method to return the optional GoogleDriveDTO
        when(googleDriveService.getGoogleDriveById(driveId)).thenReturn(optionalGoogleDriveDTO);

        // Call the controller method
        ResponseEntity<GoogleDriveDTO> response = googleDriveController.getGoogleDriveById(driveId);

        // Verify the HTTP status code
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // Verify the response body
        GoogleDriveDTO responseBody = response.getBody();
        assertEquals(googleDriveDTO, responseBody);
    }

    @Test
     void testGetGoogleDriveById_NotFound() {
        // Prepare mock data
        Long driveId = 1L;
        Optional<GoogleDriveDTO> optionalGoogleDriveDTO = Optional.empty();

        // Mock the service method to return an empty optional
        when(googleDriveService.getGoogleDriveById(driveId)).thenReturn(optionalGoogleDriveDTO);

        // Call the controller method
        ResponseEntity<GoogleDriveDTO> response = googleDriveController.getGoogleDriveById(driveId);

        // Verify the HTTP status code for not found
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
    @Test
    void testDeleteGoogleDriveById_Success() {
        // Prepare mock data
        Long driveId = 1L;
        String successMessage = "Drive deleted successfully";

        // Mock the service method to return a success message
        when(googleDriveService.deleteGoogleDriveById(driveId)).thenReturn(ResponseEntity.ok(successMessage));

        // Call the controller method
        ResponseEntity<String> response = googleDriveController.deleteGoogleDriveById(driveId);

        // Verify the HTTP status code
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // Verify the response body
        assertEquals(successMessage, response.getBody());
    }

    @Test
     void testGetGoogleDriveByProjectId_Exists() {
        // Prepare mock data
        Long projectId = 1L;
        GoogleDriveDTO googleDriveDTO = new GoogleDriveDTO("Drive 1", projectId);
        ResponseEntity<GoogleDriveDTO> expectedResponse = ResponseEntity.ok(googleDriveDTO);

        // Mock the service method to return the expected response
        when(googleDriveService.getGoogleDriveByProjectId(projectId)).thenReturn(expectedResponse);

        // Call the controller method
        ResponseEntity<GoogleDriveDTO> response = googleDriveController.getGoogleDriveByProjectId(projectId);

        // Verify the HTTP status code
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // Verify the response body
        assertEquals(expectedResponse.getBody(), response.getBody());
    }

    @Test
     void testGetGoogleDriveByProjectId_NotFound() {
        // Prepare mock data
        Long projectId = 1L;
        ResponseEntity<GoogleDriveDTO> expectedResponse = ResponseEntity.notFound().build();

        // Mock the service method to return a not found response
        when(googleDriveService.getGoogleDriveByProjectId(projectId)).thenReturn(expectedResponse);

        // Call the controller method
        ResponseEntity<GoogleDriveDTO> response = googleDriveController.getGoogleDriveByProjectId(projectId);

        // Verify the HTTP status code for not found
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}

