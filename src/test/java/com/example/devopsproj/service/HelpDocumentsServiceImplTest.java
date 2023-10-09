package com.example.devopsproj.service;

import com.example.devopsproj.dto.responsedto.HelpDocumentsDTO;
import com.example.devopsproj.exceptions.NotFoundException;
import com.example.devopsproj.model.HelpDocuments;
import com.example.devopsproj.model.Project;
import com.example.devopsproj.repository.HelpDocumentsRepository;
import com.example.devopsproj.repository.ProjectRepository;
import com.example.devopsproj.service.implementations.HelpDocumentsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HelpDocumentsServiceImplTest {

    @InjectMocks
    private HelpDocumentsServiceImpl helpDocumentsService;
    @Mock
    private HelpDocumentsRepository helpDocumentsRepository;
    @Mock
    private ProjectRepository projectRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ----- SUCCESS -----

    @Test
    void testUploadFiles_Success() throws IOException {
        long projectId = 1L;
        byte[] fileContent = "Sample file content".getBytes();
        String fileExtension = "txt";

        Project project = new Project();
        project.setProjectId(projectId);

        MockMultipartFile multipartFile = new MockMultipartFile(
                "projectFile", "sample.txt", "text/plain", fileContent);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        ResponseEntity<Object> response = helpDocumentsService.uploadFiles(projectId, multipartFile, fileExtension);

        assertEquals("File uploaded successfully", response.getBody());

        ArgumentCaptor<HelpDocuments> helpDocumentsCaptor = ArgumentCaptor.forClass(HelpDocuments.class);
        verify(helpDocumentsRepository, times(1)).save(helpDocumentsCaptor.capture());

        HelpDocuments savedHelpDocuments = helpDocumentsCaptor.getValue();

        // Verify that the saved HelpDocuments object matches the expected values
        assertEquals(project, savedHelpDocuments.getProject());
        assertEquals("sample.txt", savedHelpDocuments.getFileName());
        assertArrayEquals(fileContent, savedHelpDocuments.getData());
        assertEquals("txt", savedHelpDocuments.getFileExtension());
    }


    @Test
    void testSaveFile_Success() throws IOException {
        HelpDocuments helpDocuments = new HelpDocuments();
        MultipartFile file = createMockMultipartFile();
        String fileExtension = "pdf";

        helpDocumentsService.saveFile(helpDocuments, file, fileExtension);

        assertEquals("test.pdf", helpDocuments.getFileName());
        assertArrayEquals("Mock PDF Content".getBytes(), helpDocuments.getData());
        assertEquals("pdf", helpDocuments.getFileExtension());
    }

    @Test
    void testGetFileExtension_ValidFilename() {
        MultipartFile file = new MockMultipartFile("file", "example.pdf", "application/pdf", new byte[0]);

        String fileExtension = helpDocumentsService.getFileExtension(file);

        assertEquals("pdf", fileExtension);
    }

    @Test
    void testGetAllDocumentsByProjectId_Success() {
        // Arrange
        Long projectId = 1L;

        List<HelpDocuments> pdfFiles = new ArrayList<>();
        HelpDocuments pdfFile1 = new HelpDocuments();
        pdfFile1.setHelpDocumentId(1L);
        pdfFile1.setFileName("Document1.pdf");

        Project project1 = new Project();
        project1.setProjectId(projectId);

        pdfFile1.setProject(project1);
        pdfFiles.add(pdfFile1);

        HelpDocuments pdfFile2 = new HelpDocuments();
        pdfFile2.setHelpDocumentId(2L);
        pdfFile2.setFileName("Document2.pdf");

        Project project2 = new Project();
        project2.setProjectId(projectId + 1);

        pdfFile2.setProject(project2);
        pdfFiles.add(pdfFile2);
        when(helpDocumentsRepository.findAll()).thenReturn(pdfFiles);

        // Act
        List<HelpDocumentsDTO> result = helpDocumentsService.getAllDocumentsByProjectId(projectId);

        // Assert
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getHelpDocumentId());
        assertEquals("Document1.pdf", result.get(0).getFileName());
    }

    @Test
    void testGetDocumentById_ValidId() {
        Long fileId = 1L;
        HelpDocuments helpDocument = new HelpDocuments();
        helpDocument.setHelpDocumentId(fileId);
        helpDocument.setFileName("Document1.pdf");

        when(helpDocumentsRepository.findById(fileId)).thenReturn(Optional.of(helpDocument));

        Optional<HelpDocumentsDTO> result = helpDocumentsService.getDocumentById(fileId);

        assertTrue(result.isPresent());
        assertEquals(fileId, result.get().getHelpDocumentId());
        assertEquals("Document1.pdf", result.get().getFileName());
    }

    @Test
    void testGetPdfFile_ValidFileName() {
        String fileName = "Document1.pdf";
        HelpDocuments expectedDocument = new HelpDocuments();
        expectedDocument.setFileName(fileName);

        when(helpDocumentsRepository.findByFileName(fileName)).thenReturn(expectedDocument);

        HelpDocuments result = helpDocumentsService.getPdfFile(fileName);

        assertNotNull(result);
        assertEquals(fileName, result.getFileName());
    }

    @Test
    void testDeleteDocument_Success() {
        Long fileId = 1L;
        doNothing().when(helpDocumentsRepository).deleteById(fileId);

        assertDoesNotThrow(() -> helpDocumentsService.deleteDocument(fileId));

        verify(helpDocumentsRepository, times(1)).deleteById(fileId);
    }


    // ----- FAILURE -----

    @Test
    void testUploadFiles_ProjectNotFound() throws IOException {
        long projectId = 1L;
        MultipartFile projectFile = createMockMultipartFile();
        String fileExtension = "pdf";

        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            helpDocumentsService.uploadFiles(projectId, projectFile, fileExtension);
        });

        // Verify that saveFile and helpDocumentsRepository.save are not called
        verify(helpDocumentsRepository, never()).save(any());
    }

    @Test
    void testSaveFile_NullFile() throws IOException {
        HelpDocuments helpDocuments = new HelpDocuments();
        MultipartFile file = null;
        String fileExtension = "pdf";

        helpDocumentsService.saveFile(helpDocuments, file, fileExtension);

        assertNull(helpDocuments.getFileName());
        assertNull(helpDocuments.getData());
        assertNull(helpDocuments.getFileExtension());
    }

    @Test
    void testGetFileExtension_FilenameWithoutExtension() {
        MultipartFile file1 = new MockMultipartFile("file", "fileWithoutExtension", "text/plain", new byte[0]);
        MultipartFile file2 = new MockMultipartFile("file", null, "text/plain", new byte[0]);
        MultipartFile file3 = new MockMultipartFile("file", "", "text/plain", new byte[0]);

        String fileExtension1 = helpDocumentsService.getFileExtension(file1);
        String fileExtension2 = helpDocumentsService.getFileExtension(file2);
        String fileExtension3 = helpDocumentsService.getFileExtension(file3);

        assertNull(fileExtension1);
        assertNull(fileExtension2);
        assertNull(fileExtension3);
    }

    @Test
    void testGetAllDocumentsByProjectId_NoFilesFound() {
        MockitoAnnotations.openMocks(this);
        Long projectId = 1L;
        List<HelpDocuments> pdfFiles = new ArrayList<>();
        when(helpDocumentsRepository.findAll()).thenReturn(pdfFiles);

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> helpDocumentsService.getAllDocumentsByProjectId(projectId));
        assertEquals("Files Not Found", exception.getMessage());
    }

    @Test
    void testGetAllDocumentsByProjectId_NullFileName() {
        Long projectId = 1L;

        List<HelpDocuments> pdfFiles = new ArrayList<>();

        HelpDocuments pdfFile1 = new HelpDocuments();
        pdfFile1.setHelpDocumentId(1L);
        pdfFile1.setFileName(null); // Null filename

        Project project = new Project();
        project.setProjectId(projectId);
        pdfFile1.setProject(project);
        pdfFiles.add(pdfFile1);
        when(helpDocumentsRepository.findAll()).thenReturn(pdfFiles);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            helpDocumentsService.getAllDocumentsByProjectId(projectId);
        });

        assertEquals("Files Not Found", exception.getMessage());
    }

    @Test
    void testGetDocumentById_InvalidId() {
        Long fileId = 1L;
        when(helpDocumentsRepository.findById(fileId)).thenReturn(Optional.empty());

        Optional<HelpDocumentsDTO> result = helpDocumentsService.getDocumentById(fileId);

        assertFalse(result.isPresent());
    }

    @Test
    void testGetPdfFile_InvalidFileName() {
        String fileName = "NonExistent.pdf";
        when(helpDocumentsRepository.findByFileName(fileName)).thenReturn(null);

        HelpDocuments result = helpDocumentsService.getPdfFile(fileName);

        assertNull(result);
    }

    private MultipartFile createMockMultipartFile() {
        return new MockMultipartFile("file", "test.pdf", "application/pdf", "Mock PDF Content".getBytes());
    }
}
