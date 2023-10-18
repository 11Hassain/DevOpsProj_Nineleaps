package com.example.devopsproj.controller;

import com.example.devopsproj.service.interfaces.HelpDocumentsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class HelpDocumentsControllerTest {

    @InjectMocks
    private HelpDocumentsController helpDocumentsController;

    @Mock
    private HelpDocumentsService helpDocumentsService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
     void testUploadFile_Success() throws IOException {
        // Prepare mock data
        long projectId = 1L;
        String fileExtension = "pdf";
        MultipartFile mockFile = new MockMultipartFile("projectFile", "sample.pdf", "application/pdf", "file content".getBytes());
        ResponseEntity<Object> expectedResponse = ResponseEntity.status(HttpStatus.CREATED).build();

        // Mock the service method to return the expected response
        when(helpDocumentsService.getFileExtension(mockFile)).thenReturn(fileExtension);
        when(helpDocumentsService.uploadFiles(projectId, mockFile, fileExtension)).thenReturn(expectedResponse);

        // Call the controller method
        ResponseEntity<Object> response = helpDocumentsController.uploadFile(projectId, mockFile);

        // Verify the HTTP status code
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
     void testUploadFile_NoFile() throws IOException {
        // Prepare mock data
        long projectId = 1L;

        // Mock the service method to return a response
        when(helpDocumentsController.uploadFile(projectId, null))
                .thenReturn(ResponseEntity.badRequest().build());

        // Call the controller method without providing a file
        ResponseEntity<Object> response = helpDocumentsController.uploadFile(projectId, null);

        // Verify the HTTP status code for bad request
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
    @Test
     void testGetPdfFilesList_Success() {
        // Prepare mock data
        long projectId = 1L;
        List<String> pdfFiles = new ArrayList<>();
        pdfFiles.add("file1.pdf");
        pdfFiles.add("file2.pdf");

        // Create a ResponseEntity with the mock PDF files list as the body
        ResponseEntity<Object> responseEntity = ResponseEntity.ok(pdfFiles);

        // Mock the service method to return the ResponseEntity
        when(helpDocumentsService.getPdfFilesList(projectId)).thenReturn(responseEntity);

        // Call the controller method
        ResponseEntity<Object> response = helpDocumentsController.getPdfFilesList(projectId);

        // Verify the HTTP status code
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // Verify the response body (list of PDF files)
        List<String> responsePdfFiles = (List<String>) response.getBody();
        assertEquals(pdfFiles, responsePdfFiles);
    }



    @Test
     void testGetPdfFilesList_NoFilesFound() {
        // Prepare mock data
        long projectId = 1L;

        // Mock the service method to return an empty ResponseEntity
        when(helpDocumentsService.getPdfFilesList(projectId)).thenReturn(ResponseEntity.ok(new ArrayList<>()));

        // Call the controller method
        ResponseEntity<Object> response = helpDocumentsController.getPdfFilesList(projectId);

        // Verify the HTTP status code (OK since no error occurred)
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // Verify that the response body is an empty list
        List<String> responsePdfFiles = (List<String>) response.getBody();
        assertEquals(0, responsePdfFiles.size());
    }




    @Test
     void testDownloadPdfFile_Success() {
        // Prepare test data
        String fileName = "sample.pdf";
        byte[] pdfContent = "Sample PDF content".getBytes(); // Replace with actual PDF content

        // Mock the service method to return a custom ResponseEntity
        when(helpDocumentsService.downloadPdfFile(anyString())).thenReturn(
                ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                        .body(pdfContent)
        );

        // Call the controller method
        ResponseEntity<?> response = helpDocumentsController.downloadPdfFile(fileName);

        // Verify the HTTP status code
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // Verify the Content-Type header
        HttpHeaders headers = response.getHeaders();
        assertEquals(MediaType.APPLICATION_OCTET_STREAM, headers.getContentType());

        // Verify the Content-Disposition header
        String expectedContentDisposition = "attachment; filename=\"" + fileName + "\"";
        assertEquals(expectedContentDisposition, headers.getFirst(HttpHeaders.CONTENT_DISPOSITION));

        // Verify the PDF content
        assertArrayEquals(pdfContent, (byte[]) response.getBody());
    }

    @Test
     void testDownloadPdfFile_FileNotFound() {
        // Prepare mock data
        String fileName = "non_existent.pdf";

        // Mock the service method to return a ResponseEntity with NOT_FOUND status
        when(helpDocumentsService.downloadPdfFile(fileName)).thenReturn(ResponseEntity.notFound().build());

        // Call the controller method
        ResponseEntity<?> response = helpDocumentsController.downloadPdfFile(fileName);

        // Verify the HTTP status code (NOT_FOUND)
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testDeleteFile() {
        // Arrange
        Long fileId = 1L;
        ResponseEntity<String> responseEntity = ResponseEntity.ok("File deleted successfully");

        when(helpDocumentsService.deleteDocument(fileId)).thenReturn(responseEntity);

        // Act
        ResponseEntity<String> response = helpDocumentsController.deleteFile(fileId);

        // Assert
        verify(helpDocumentsService, times(1)).deleteDocument(fileId);
        assertEquals(responseEntity, response);
    }


}
