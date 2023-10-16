package com.example.devopsproj.controller;

import com.example.devopsproj.dto.responsedto.HelpDocumentsDTO;
import com.example.devopsproj.exceptions.NotFoundException;
import com.example.devopsproj.model.HelpDocuments;
import com.example.devopsproj.service.implementations.HelpDocumentsServiceImpl;
import com.example.devopsproj.service.implementations.JwtServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class HelpDocumentsControllerTest {

    @InjectMocks
    private HelpDocumentsController helpDocumentsController;
    @Mock
    private HelpDocumentsServiceImpl helpDocumentsService;
    @Mock
    private JwtServiceImpl jwtService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    class UploadFileTest {
        @Test
        @DisplayName("Testing success case with valid token")
        void testUploadFile_ValidToken_ValidFile() throws IOException {
            long projectId = 1L;
            String accessToken = "valid-access-token";
            MultipartFile projectFile = createMockMultipartFile();
            String fileExtension = "txt";

            when(jwtService.isTokenTrue(accessToken)).thenReturn(true);
            when(helpDocumentsService.getFileExtension(projectFile)).thenReturn(fileExtension);
            when(helpDocumentsService.uploadFiles(projectId, projectFile, fileExtension)).thenReturn(
                    ResponseEntity.ok("File uploaded successfully")
            );

            ResponseEntity<Object> response = helpDocumentsController.uploadFile(projectId, projectFile, accessToken);

            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("File uploaded successfully", response.getBody());

            verify(jwtService).isTokenTrue(accessToken);
            verify(helpDocumentsService).getFileExtension(projectFile);
            verify(helpDocumentsService).uploadFiles(projectId, projectFile, fileExtension);
        }

        @Test
        @DisplayName("Testing invalid parameters case (Bad Request)")
        void testUploadFile_InvalidParameters() throws IOException {
            long projectId = 1L;
            String accessToken = "valid-access-token";
            MultipartFile projectFile = createMockMultipartFile();
            String fileExtension = "txt";

            when(jwtService.isTokenTrue(accessToken)).thenReturn(true);
            when(helpDocumentsService.getFileExtension(projectFile)).thenReturn(fileExtension);
            when(helpDocumentsService.uploadFiles(projectId, projectFile, fileExtension))
                    .thenThrow(new IllegalArgumentException("Invalid parameters"));

            ResponseEntity<Object> response = helpDocumentsController.uploadFile(projectId, projectFile, accessToken);

            assertNotNull(response);
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertEquals("Invalid parameters", response.getBody());
        }

        @Test
        @DisplayName("Testing failure case with invalid token")
        void testUploadFile_InvalidToken() throws IOException {
            long projectId = 1L;
            MultipartFile projectFile = createMockMultipartFile();

            when(jwtService.isTokenTrue(anyString())).thenReturn(false);

            ResponseEntity<Object> response = helpDocumentsController.uploadFile(projectId, projectFile, "invalid-access-token");

            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            assertEquals("Invalid Token", response.getBody());
        }
    }

    @Nested
    class GetPdfFilesListTest {
        @Test
        @DisplayName("Testing success case with valid token")
        void testGetPdfFilesList_ValidToken_PdfFilesFound() {
            long projectId = 1L;
            String accessToken = "valid-access-token";

            when(jwtService.isTokenTrue(accessToken)).thenReturn(true);

            List<HelpDocumentsDTO> pdfFilesList = new ArrayList<>();
            pdfFilesList.add(new HelpDocumentsDTO());
            when(helpDocumentsService.getAllDocumentsByProjectId(projectId)).thenReturn(pdfFilesList);

            ResponseEntity<Object> response = helpDocumentsController.getPdfFilesList(projectId, accessToken);

            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(pdfFilesList, response.getBody());
        }

        @Test
        @DisplayName("Testing files not found case")
        void testGetPdfFilesList_ValidToken_NoPdfFilesFound() {
            long projectId = 1L;
            String accessToken = "valid-access-token";

            when(jwtService.isTokenTrue(accessToken)).thenReturn(true);

            when(helpDocumentsService.getAllDocumentsByProjectId(projectId))
                    .thenThrow(new NotFoundException("No PDF files found"));

            ResponseEntity<Object> response = helpDocumentsController.getPdfFilesList(projectId, accessToken);

            assertNotNull(response);
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertEquals("No PDF files found", response.getBody());
        }

        @Test
        @DisplayName("Testing failure case with invalid token")
        void testGetPdfFilesList_InvalidToken(){
            long projectId = 1L;

            when(jwtService.isTokenTrue(anyString())).thenReturn(false);

            ResponseEntity<Object> response = helpDocumentsController.getPdfFilesList(projectId,"invalid-access-token");

            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            assertEquals("Invalid Token", response.getBody());
        }
    }

    @Nested
    class DownloadPdfFileTest {
        @Test
        @DisplayName("Testing success case with valid token")
        void testDownloadPdfFile_ValidToken_PdfFileFound() {
            String fileName = "example.pdf";
            String accessToken = "valid-access-token";

            when(jwtService.isTokenTrue(accessToken)).thenReturn(true);

            HelpDocuments pdfFile = new HelpDocuments();
            pdfFile.setFileName(fileName);
            pdfFile.setData(new byte[]{ });
            when(helpDocumentsService.getPdfFile(fileName)).thenReturn(pdfFile);

            ResponseEntity<Object> response = helpDocumentsController.downloadPdfFile(fileName, accessToken);

            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
        }

        @Test
        @DisplayName("Testing file not found case")
        void testDownloadPdfFile_ValidToken_PdfFileNotFound() {
            String fileName = "non-existent.pdf";
            String accessToken = "valid-access-token";

            when(jwtService.isTokenTrue(accessToken)).thenReturn(true);

            when(helpDocumentsService.getPdfFile(fileName)).thenReturn(null);

            ResponseEntity<Object> response = helpDocumentsController.downloadPdfFile(fileName, accessToken);

            assertNotNull(response);
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        }

        @Test
        @DisplayName("Testing failure case with invalid token")
        void testDownloadPdfFile_InvalidToken() {
            String fileName = "Project.txt";

            when(jwtService.isTokenTrue(anyString())).thenReturn(false);

            ResponseEntity<Object> response = helpDocumentsController.downloadPdfFile(fileName,"invalid-access-token");

            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            assertEquals("Invalid Token", response.getBody());
        }
    }

    @Nested
    class DeleteFileTest {
        @Test
        @DisplayName("Testing success case with valid token")
        void testDeleteFile_ValidToken_DocumentFoundAndDeleted() {
            Long fileId = 1L;
            String accessToken = "valid-access-token";

            when(jwtService.isTokenTrue(accessToken)).thenReturn(true);

            HelpDocumentsDTO documentDTO = new HelpDocumentsDTO();
            when(helpDocumentsService.getDocumentById(fileId)).thenReturn(Optional.of(documentDTO));

            doNothing().when(helpDocumentsService).deleteDocument(fileId);

            ResponseEntity<String> response = helpDocumentsController.deleteFile(fileId, accessToken);

            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("Document deleted successfully", response.getBody());
        }

        @Test
        @DisplayName("Testing document not found case")
        void testDeleteFile_ValidToken_DocumentNotFound() {
            Long fileId = 1L;
            String accessToken = "valid-access-token";

            when(jwtService.isTokenTrue(accessToken)).thenReturn(true);
            when(helpDocumentsService.getDocumentById(fileId)).thenReturn(Optional.empty());

            ResponseEntity<String> response = helpDocumentsController.deleteFile(fileId, accessToken);

            assertNotNull(response);
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertEquals("Document not found", response.getBody());
        }

        @Test
        @DisplayName("Testing failure case with invalid token")
        void testDeleteFile_InvalidToken() {
            Long fileId = 1L;

            when(jwtService.isTokenTrue(anyString())).thenReturn(false);

            ResponseEntity<String> response = helpDocumentsController.deleteFile(fileId,"invalid-access-token");

            assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            assertEquals("Invalid Token", response.getBody());
        }
    }

    // Utility method to create a mock MultipartFile for testing
    private MultipartFile createMockMultipartFile() throws IOException {
        byte[] contentBytes = "This is a test file.".getBytes();
        return new MockMultipartFile("projectFile", "test-file.txt", "text/plain", contentBytes);
    }
}
