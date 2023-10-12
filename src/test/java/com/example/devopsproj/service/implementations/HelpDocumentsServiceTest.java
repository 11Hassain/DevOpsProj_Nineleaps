package com.example.devopsproj.service.implementations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.example.devopsproj.dto.responsedto.HelpDocumentsDTO;
import com.example.devopsproj.model.HelpDocuments;
import com.example.devopsproj.model.Project;
import com.example.devopsproj.repository.GoogleDriveRepository;
import com.example.devopsproj.repository.HelpDocumentsRepository;
import com.example.devopsproj.repository.ProjectRepository;
import com.example.devopsproj.service.interfaces.ProjectService;
import org.junit.jupiter.api.Test;
import org.springframework.http.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

import static org.mockito.Mockito.*;

public class HelpDocumentsServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private HelpDocumentsRepository helpDocumentsRepository;

    @Mock
    private ProjectService projectService;

    @Mock
    private GoogleDriveRepository googleDriveRepository;

    @InjectMocks
    private HelpDocumentsServiceImpl helpDocumentsService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testUploadFiles() throws IOException {
        // Create a sample project ID and extension
        long projectId = 1L;
        String fileExtension = "pdf";

        // Create a sample project
        Project project = new Project();
        project.setProjectId(projectId);

        // Mock behavior for projectRepository
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        // Create a MockMultipartFile as a mock project file
        MockMultipartFile projectFile = new MockMultipartFile(
                "file",               // file name
                "sample.pdf",         // original file name
                "application/pdf",    // content type
                new byte[0]           // file content as bytes (empty in this example)
        );

        // Create a HelpDocuments object to be saved
        HelpDocuments helpDocuments = new HelpDocuments();

        // Mock behavior for saveFile method (if needed)

        // Mock behavior for helpDocumentsRepository
        when(helpDocumentsRepository.save(any())).thenReturn(helpDocuments);

        // Call the uploadFiles method
        ResponseEntity<Object> response = helpDocumentsService.uploadFiles(projectId, projectFile, fileExtension);

        // Verify that projectRepository.findById was called
        verify(projectRepository, times(1)).findById(projectId);

        // Verify that helpDocumentsRepository.save was called
        verify(helpDocumentsRepository, times(1)).save(any());

        // Assert the expected ResponseEntity status code (OK)
        assert(response.getStatusCodeValue() == 200);

        // Assert the expected success message
        assert(response.getBody().equals("File uploaded successfully"));
    }
    @Test
    public void testGetFileExtension() {
        // Create a mock MultipartFile with a sample file name
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",               // file name
                "sample.pdf",         // original file name
                "application/pdf",    // content type
                new byte[0]           // file content as bytes (empty in this example)
        );

        // Call the getFileExtension method
        String fileExtension = helpDocumentsService.getFileExtension(mockFile);

        // Assert that the file extension is as expected (in this case, "pdf")
        assertEquals("pdf", fileExtension);
    }

    @Test
    public void testGetFileExtensionWithNoExtensionn() {
        // Create a mock MultipartFile with a sample file name without an extension
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",               // file name
                "sample",             // original file name without an extension
                "application/pdf",    // content type
                new byte[0]           // file content as bytes (empty in this example)
        );

        // Call the getFileExtension method
        String fileExtension = helpDocumentsService.getFileExtension(mockFile);

        // Assert that the file extension is null since there's no extension in the file name
        assertNull(fileExtension);
    }

    @Test
    public void testGetFileExtensionWithInvalidParameters() {
        // Create a mock MultipartFile with a null original filename
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",               // file name
                null,                 // original file name is null
                "application/pdf",    // content type
                new byte[0]           // file content as bytes (empty in this example)
        );

        // Verify that the getFileExtension method does not throw an exception
        assertDoesNotThrow(() -> {
            helpDocumentsService.getFileExtension(mockFile);
        });
    }
    @Test
    public void testGetPdfFilesList() {
        // Create a sample project ID
        long projectId = 1L;

        // Create a list of sample HelpDocuments
        List<HelpDocuments> pdfFiles = new ArrayList<>();

        // Add some sample HelpDocuments to the list
        HelpDocuments helpDocument1 = new HelpDocuments();
        helpDocument1.setHelpDocumentId(1L);
        helpDocument1.setFileName("document1.pdf");
        Project project1 = new Project();
        project1.setProjectId(projectId);
        helpDocument1.setProject(project1);
        pdfFiles.add(helpDocument1);

        // Mock behavior for helpDocumentsRepository
        when(helpDocumentsRepository.findAll()).thenReturn(pdfFiles);

        // Call the getPdfFilesList method
        ResponseEntity<Object> response = helpDocumentsService.getPdfFilesList(projectId);

        // Verify that helpDocumentsRepository.findAll was called
        verify(helpDocumentsRepository, times(1)).findAll();

        // Assert the expected ResponseEntity status code (OK)
        assertEquals(200, response.getStatusCodeValue());

        // Assert the expected list of HelpDocumentsDTO in the response body
        List<HelpDocumentsDTO> fileInfos = (List<HelpDocumentsDTO>) response.getBody();
        assertNotNull(fileInfos);
        assertEquals(1, fileInfos.size()); // Assuming only one matching HelpDocument
        assertEquals(1L, fileInfos.get(0).getHelpDocumentId()); // Check the HelpDocumentDTO ID
        assertEquals("document1.pdf", fileInfos.get(0).getFileName()); // Check the HelpDocumentDTO file name
    }

    @Test
    public void testGetPdfFilesListEmptyResult() {
        // Create a sample project ID with no matching HelpDocuments
        long projectId = 1L;

        // Create an empty list of HelpDocuments
        List<HelpDocuments> pdfFiles = new ArrayList<>();

        // Mock behavior for helpDocumentsRepository
        when(helpDocumentsRepository.findAll()).thenReturn(pdfFiles);

        // Call the getPdfFilesList method
        ResponseEntity<Object> response = helpDocumentsService.getPdfFilesList(projectId);

        // Verify that helpDocumentsRepository.findAll was called
        verify(helpDocumentsRepository, times(1)).findAll();

        // Assert the expected ResponseEntity status code (NOT_FOUND)
        assertEquals(404, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

//    @Test
//    public void testGetFileExtensionWithValidFile() {
//        // Arrange
//        HelpDocumentsRepository helpDocumentsRepository = Mockito.mock(HelpDocumentsRepository.class);
//        ProjectRepository projectRepository = Mockito.mock(ProjectRepository.class);
//        ProjectService projectService = Mockito.mock(ProjectService.class);
//        GoogleDriveRepository googleDriveRepository = Mockito.mock(GoogleDriveRepository.class);
//        HelpDocumentsServiceImpl helpDocumentsService = new HelpDocumentsServiceImpl(
//                projectRepository, helpDocumentsRepository, projectService, googleDriveRepository
//        );
//
//        MultipartFile file = Mockito.mock(MultipartFile.class);
//        Mockito.when(file.getOriginalFilename()).thenReturn("example.pdf");
//
//        // Act
//        String fileExtension = helpDocumentsService.getFileExtension(file);
//
//        // Assert
//        assertEquals("pdf", fileExtension);
//    }

    @Test
    public void testDownloadPdfFile() {
        // Create a sample file name
        String fileName = "document1.pdf";

        // Create a sample HelpDocuments object
        HelpDocuments pdfFile = new HelpDocuments();
        pdfFile.setFileName(fileName);
        pdfFile.setData(new byte[]{1, 2, 3}); // Sample file data

        // Mock behavior for helpDocumentsRepository.findByFileName
        when(helpDocumentsRepository.findByFileName(fileName)).thenReturn(pdfFile);

        // Call the downloadPdfFile method
        ResponseEntity<byte[]> response = helpDocumentsService.downloadPdfFile(fileName);

        // Verify that helpDocumentsRepository.findByFileName was called
        verify(helpDocumentsRepository, times(1)).findByFileName(fileName);

        // Assert the expected ResponseEntity status code (OK)
        assertEquals(200, response.getStatusCodeValue());

        // Assert the expected response headers
        HttpHeaders headers = response.getHeaders();
        assertNotNull(headers);
        assertEquals(MediaType.APPLICATION_OCTET_STREAM, headers.getContentType());

        // Assert the expected response header for Content-Disposition with UTF-8 encoding
        String expectedContentDisposition = "attachment; filename=\"=?UTF-8?Q?document1.pdf?=\"";
        assertTrue(headers.containsKey(HttpHeaders.CONTENT_DISPOSITION));
        assertTrue(headers.getFirst(HttpHeaders.CONTENT_DISPOSITION).startsWith(expectedContentDisposition));

        // Assert the expected response body (file data)
        assertArrayEquals(new byte[]{1, 2, 3}, response.getBody());
    }



    @Test
    public void testDownloadPdfFileNotFound() {
        // Create a sample file name that does not exist in the repository
        String fileName = "non_existent.pdf";

        // Mock behavior for helpDocumentsRepository.findByFileName (returns null)
        when(helpDocumentsRepository.findByFileName(fileName)).thenReturn(null);

        // Call the downloadPdfFile method
        ResponseEntity<byte[]> response = helpDocumentsService.downloadPdfFile(fileName);

        // Verify that helpDocumentsRepository.findByFileName was called
        verify(helpDocumentsRepository, times(1)).findByFileName(fileName);

        // Assert the expected ResponseEntity status code (NOT_FOUND)
        assertEquals(404, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    public void testGetDocumentById() {
        // Create a sample HelpDocuments object
        Long fileId = 1L;
        String fileName = "document1.pdf";
        HelpDocuments helpDocument = new HelpDocuments();
        helpDocument.setHelpDocumentId(fileId);
        helpDocument.setFileName(fileName);

        // Mock behavior for helpDocumentsRepository.findById
        when(helpDocumentsRepository.findById(fileId)).thenReturn(Optional.of(helpDocument));

        // Call the getDocumentById method
        Optional<HelpDocumentsDTO> result = helpDocumentsService.getDocumentById(fileId);

        // Verify that helpDocumentsRepository.findById was called
        verify(helpDocumentsRepository, times(1)).findById(fileId);

        // Assert that the result is present and has the expected values
        assertTrue(result.isPresent());
        HelpDocumentsDTO helpDocumentsDTO = result.get();
        assertEquals(fileId, helpDocumentsDTO.getHelpDocumentId());
        assertEquals(fileName, helpDocumentsDTO.getFileName());
    }

    @Test
    public void testGetDocumentByIdWhenNotExists() {
        // Create a sample non-existent file ID
        Long fileId = 999L;

        // Mock behavior for helpDocumentsRepository.findById
        when(helpDocumentsRepository.findById(fileId)).thenReturn(Optional.empty());

        // Call the getDocumentById method
        Optional<HelpDocumentsDTO> result = helpDocumentsService.getDocumentById(fileId);

        // Verify that helpDocumentsRepository.findById was called
        verify(helpDocumentsRepository, times(1)).findById(fileId);

        // Assert that the result is empty
        assertFalse(result.isPresent());
    }

    @Test
    public void testDeleteDocument() {
        // Create a sample file ID
        Long fileId = 1L;

        // Mock behavior for helpDocumentsRepository.findById
        when(helpDocumentsRepository.findById(fileId)).thenReturn(Optional.of(new HelpDocuments()));

        // Call the deleteDocument method
        ResponseEntity<String> response = helpDocumentsService.deleteDocument(fileId);

        // Verify that helpDocumentsRepository.findById and helpDocumentsRepository.deleteById were called
        verify(helpDocumentsRepository, times(1)).findById(fileId);
        verify(helpDocumentsRepository, times(1)).deleteById(fileId);

        // Assert the expected ResponseEntity status code (OK)
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // Assert the expected response message
        assertEquals("Document deleted successfully", response.getBody());
    }

    @Test
    public void testDeleteDocumentWhenNotExists() {
        // Create a sample non-existent file ID
        Long fileId = 999L;

        // Mock behavior for helpDocumentsRepository.findById
        when(helpDocumentsRepository.findById(fileId)).thenReturn(Optional.empty());

        // Call the deleteDocument method
        ResponseEntity<String> response = helpDocumentsService.deleteDocument(fileId);

        // Verify that helpDocumentsRepository.findById was called
        verify(helpDocumentsRepository, times(1)).findById(fileId);

        // Assert the expected ResponseEntity status code (NOT_FOUND)
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        // Assert the expected response message
        assertEquals("Document not found", response.getBody());
    }
    @Test
    void testGetFileExtensionn() {
        // Create a mock MultipartFile
        MultipartFile mockFile = mock(MultipartFile.class);

        // Test when the originalFilename is not null
        when(mockFile.getOriginalFilename()).thenReturn("example.txt");
        HelpDocumentsServiceImpl service = new HelpDocumentsServiceImpl();
        String extension = service.getFileExtension(mockFile);
        assertEquals("txt", extension);

        // Test when the originalFilename is null
        when(mockFile.getOriginalFilename()).thenReturn(null);
        extension = service.getFileExtension(mockFile);
        assertNull(extension);

        // Test when an IllegalArgumentException is caught
        when(mockFile.getOriginalFilename()).thenThrow(new IllegalArgumentException("Invalid parameters"));
        assertThrows(IllegalArgumentException.class, () -> service.getFileExtension(mockFile));
    }

}


