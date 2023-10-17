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

    // Upload a file associated with a project
    @Override
    public ResponseEntity<Object> uploadFiles(long projectId, MultipartFile projectFile, String fileExtension) throws IOException {
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

        // Return a success response with a message
        return ResponseEntity.ok("File uploaded successfully");
    }

    // Extract and return the file extension from a multipart file
    @Override
    public String getFileExtension(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null) {
            int dotIndex = originalFilename.lastIndexOf('.');
            if (dotIndex >= 0 && dotIndex < originalFilename.length() - 1) {
                return originalFilename.substring(dotIndex + 1).toLowerCase();
            }
        }
        throw new IllegalArgumentException("Invalid parameters");
    }


    @Override
    public ResponseEntity<Object> getPdfFilesList(long projectId) {
        List<HelpDocuments> pdfFiles = helpDocumentsRepository.findAll();

        List<HelpDocumentsDTO> fileInfos = pdfFiles.stream()
                .filter(pdfFile -> pdfFile != null && pdfFile.getProject() != null && pdfFile.getProject().getProjectId() == projectId)
                .map(pdfFile -> new HelpDocumentsDTO(pdfFile.getHelpDocumentId(), pdfFile.getFileName()))
                .filter(helpDoc -> helpDoc.getFileName() != null) // Filter out any remaining null file names
                .toList(); // Replace .collect(Collectors.toList()) with .toList()

        if (fileInfos.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok().body(fileInfos);
    }

    @Override
    public ResponseEntity<byte[]> downloadPdfFile(String fileName) {
        HelpDocuments pdfFile = helpDocumentsRepository.findByFileName(fileName);
        if (pdfFile == null) {
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

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfFile.getData());
    }




    // Get information about a document by its ID
    @Override
    public Optional<HelpDocumentsDTO> getDocumentById(Long fileId) {
        // Retrieve the HelpDocuments object by its ID
        Optional<HelpDocuments> helpDocuments = helpDocumentsRepository.findById(fileId);
        if (helpDocuments.isPresent()) {
            HelpDocuments documents = helpDocuments.get();
            HelpDocumentsDTO helpDocumentsDTO = new HelpDocumentsDTO();
            helpDocumentsDTO.setHelpDocumentId(documents.getHelpDocumentId());
            helpDocumentsDTO.setFileName(documents.getFileName());
            return Optional.of(helpDocumentsDTO);
        } else {
            return Optional.empty();
        }
    }

    // Delete a document by its ID
    @Override
    public ResponseEntity<String> deleteDocument(Long fileId) {
        // Retrieve the HelpDocuments object by its ID
        Optional<HelpDocuments> helpDocuments = helpDocumentsRepository.findById(fileId);

        if (helpDocuments.isPresent()) {
            helpDocumentsRepository.deleteById(fileId);
            return ResponseEntity.ok("Document deleted successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Document not found");
        }
    }

    @Override
    public void saveFile(HelpDocuments helpDocuments, MultipartFile file, String fileExtension) throws IOException {
        if (file != null && !file.isEmpty()) {
            String originalFilename = file.getOriginalFilename();
            if (originalFilename != null) {
                helpDocuments.setFileName(originalFilename);
                helpDocuments.setData(file.getBytes());
                helpDocuments.setFileExtension(fileExtension);
            }
        } else {
            // Handle the case where the file is empty or null
            helpDocuments.setFileName(null);
            helpDocuments.setData(new byte[0]);
            helpDocuments.setFileExtension(fileExtension);
        }
    }

}