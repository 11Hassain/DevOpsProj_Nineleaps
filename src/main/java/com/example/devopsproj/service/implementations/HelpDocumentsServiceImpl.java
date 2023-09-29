package com.example.devopsproj.service.implementations;

import com.example.devopsproj.dto.responsedto.GoogleDriveDTO;
import com.example.devopsproj.dto.responsedto.HelpDocumentsDTO;
import com.example.devopsproj.dto.responsedto.ProjectDTO;
import com.example.devopsproj.model.GoogleDrive;
import com.example.devopsproj.model.HelpDocuments;
import com.example.devopsproj.model.Project;
import com.example.devopsproj.repository.GoogleDriveRepository;
import com.example.devopsproj.repository.HelpDocumentsRepository;
import com.example.devopsproj.repository.ProjectRepository;
import com.example.devopsproj.service.interfaces.HelpDocumentsService;
import com.example.devopsproj.service.interfaces.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        try {
            String originalFilename = file.getOriginalFilename();
            if (originalFilename != null) {
                int dotIndex = originalFilename.lastIndexOf('.');
                if (dotIndex >= 0 && dotIndex < originalFilename.length() - 1) {
                    return originalFilename.substring(dotIndex + 1).toLowerCase();
                }
            }
            return null;
        } catch (IllegalArgumentException e) {
            // Log the exception or perform any necessary error handling here
            throw new IllegalArgumentException("Invalid parameters");
        }
    }

    @Override
    public ResponseEntity<Object> getPdfFilesList(long projectId) {
        List<HelpDocuments> pdfFiles = helpDocumentsRepository.findAll();

        List<HelpDocumentsDTO> fileInfos = pdfFiles.stream()
                .filter(pdfFile -> pdfFile != null && pdfFile.getProject() != null && pdfFile.getProject().getProjectId() == projectId)
                .map(pdfFile -> new HelpDocumentsDTO(pdfFile.getHelpDocumentId(), pdfFile.getFileName()))
                .filter(helpDoc -> helpDoc.getFileName() != null) // Filter out any remaining null file names
                .collect(Collectors.toList());

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
        headers.setContentDispositionFormData("attachment", fileName);

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

    // Helper method to save file data to a HelpDocuments object
    private void saveFile(HelpDocuments helpDocuments, MultipartFile file, String fileExtension) throws IOException {
        if (file != null && !file.isEmpty()) {
            helpDocuments.setFileName(file.getOriginalFilename());
            helpDocuments.setData(file.getBytes());
            helpDocuments.setFileExtension(fileExtension);
        }
    }
}