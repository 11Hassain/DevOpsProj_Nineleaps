package com.example.devopsproj.service.implementations;
import com.example.devopsproj.dto.responsedto.HelpDocumentsDTO;
import com.example.devopsproj.model.HelpDocuments;
import com.example.devopsproj.model.Project;
import com.example.devopsproj.repository.GoogleDriveRepository;
import com.example.devopsproj.repository.HelpDocumentsRepository;
import com.example.devopsproj.repository.ProjectRepository;
import com.example.devopsproj.service.interfaces.HelpDocumentsService;
import com.example.devopsproj.service.interfaces.ProjectService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;



@Service
@RequiredArgsConstructor
public class HelpDocumentsServiceImpl implements HelpDocumentsService {

    private final ProjectRepository projectRepository;
    private final HelpDocumentsRepository helpDocumentsRepository;
    private final ProjectService projectService;
    private final GoogleDriveRepository googleDriveRepository;

    private static final Logger logger = LoggerFactory.getLogger(HelpDocumentsServiceImpl.class);

    // Upload a file associated with a project
    @Override
    public ResponseEntity<Object> uploadFiles(long projectId, MultipartFile projectFile, String fileExtension) throws IOException {
        logger.info("Uploading a file for project with ID: {}", projectId);

        // Retrieve the project by its ID or throw an exception if not found
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        // Create a HelpDocuments object to store information about the uploaded file
        HelpDocuments helpDocuments = new HelpDocuments();

        // Save the uploaded file to the HelpDocuments object
        saveFile(helpDocuments, projectFile, fileExtension);

        // Associate the HelpDocuments object with the project
        helpDocuments.setProject(project);

        // Save the HelpDocuments object to the repository
        helpDocumentsRepository.save(helpDocuments);

        logger.info("File uploaded successfully for project with ID: {}", projectId);

        // Return a success response with a message
        return ResponseEntity.ok("File uploaded successfully");
    }


    // Extract and return the file extension from a multipart file
    @Override
    public String getFileExtension(MultipartFile file) {
        logger.info("Getting file extension");

        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null) {
            int dotIndex = originalFilename.lastIndexOf('.');
            if (dotIndex >= 0 && dotIndex < originalFilename.length() - 1) {
                String fileExtension = originalFilename.substring(dotIndex + 1).toLowerCase();
                logger.info("File extension retrieved: {}", fileExtension);
                return fileExtension;
            }
        }

        logger.error("Invalid parameters for getting file extension");
        throw new IllegalArgumentException("Invalid parameters");
    }



    @Override
    public ResponseEntity<Object> getPdfFilesList(long projectId) {
        logger.info("Getting PDF files list for project ID: {}", projectId);

        List<HelpDocuments> pdfFiles = helpDocumentsRepository.findAll();

        List<HelpDocumentsDTO> fileInfos = pdfFiles.stream()
                .filter(pdfFile -> pdfFile != null && pdfFile.getProject() != null && pdfFile.getProject().getProjectId() == projectId)
                .map(pdfFile -> new HelpDocumentsDTO(pdfFile.getHelpDocumentId(), pdfFile.getFileName()))
                .filter(helpDoc -> helpDoc.getFileName() != null) // Filter out any remaining null file names
                .toList(); // Replace .collect(Collectors.toList()) with .toList()

        if (fileInfos.isEmpty()) {
            logger.info("No PDF files found for project ID: {}", projectId);
            return ResponseEntity.notFound().build();
        }

        logger.info("Retrieved {} PDF files for project ID: {}", fileInfos.size(), projectId);
        return ResponseEntity.ok().body(fileInfos);
    }


    @Override
    public ResponseEntity<byte[]> downloadPdfFile(String fileName) {
        logger.info("Downloading PDF file: {}", fileName);

        HelpDocuments pdfFile = helpDocumentsRepository.findByFileName(fileName);
        if (pdfFile == null) {
            logger.info("PDF file not found: {}", fileName);
            return ResponseEntity.notFound().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        // Set the Content-Disposition header with the correct format
        headers.setContentDisposition(
                ContentDisposition.builder("attachment")
                        .filename(fileName, StandardCharsets.UTF_8) // Ensure proper encoding
                        .build()
        );

        logger.info("Downloaded PDF file: {}", fileName);
        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfFile.getData());
    }




    // Get information about a document by its ID
    @Override
    public Optional<HelpDocumentsDTO> getDocumentById(Long fileId) {
        logger.info("Getting document by ID: {}", fileId);

        // Retrieve the HelpDocuments object by its ID
        Optional<HelpDocuments> helpDocuments = helpDocumentsRepository.findById(fileId);
        if (helpDocuments.isPresent()) {
            HelpDocuments documents = helpDocuments.get();
            HelpDocumentsDTO helpDocumentsDTO = new HelpDocumentsDTO();
            helpDocumentsDTO.setHelpDocumentId(documents.getHelpDocumentId());
            helpDocumentsDTO.setFileName(documents.getFileName());

            logger.info("Retrieved document by ID: {}", fileId);
            return Optional.of(helpDocumentsDTO);
        } else {
            logger.info("Document not found by ID: {}", fileId);
            return Optional.empty();
        }
    }




    // Delete a document by its ID
    @Override
    public ResponseEntity<String> softDeleteDocument(Long fileId) {
        logger.info("Soft deleting document by ID: {}", fileId);

        Optional<HelpDocuments> helpDocuments = helpDocumentsRepository.findById(fileId);
        if (helpDocuments.isPresent()) {
            HelpDocuments document = helpDocuments.get();
            document.setDeleted(true); // Soft delete
            helpDocumentsRepository.save(document);

            logger.info("Document with ID: {} has been soft-deleted successfully.", fileId);
            return ResponseEntity.ok("Document with ID: " + fileId + " has been soft-deleted successfully.");
        } else {
            logger.info("Document not found by ID: {}", fileId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Document not found");
        }
    }


    @Override
    public void saveFile(HelpDocuments helpDocuments, MultipartFile file, String fileExtension) throws IOException {
        logger.info("Saving file for HelpDocuments");

        if (file != null && !file.isEmpty()) {
            String originalFilename = file.getOriginalFilename();
            if (originalFilename != null) {
                helpDocuments.setFileName(originalFilename);
                helpDocuments.setData(file.getBytes());
                helpDocuments.setFileExtension(fileExtension);
                logger.info("File saved successfully for HelpDocuments");
            }
        } else {
            // Handle the case where the file is empty or null
            helpDocuments.setFileName(null);
            helpDocuments.setData(new byte[0]);
            helpDocuments.setFileExtension(fileExtension);
            logger.warn("Empty or null file received for HelpDocuments");
        }
    }


}