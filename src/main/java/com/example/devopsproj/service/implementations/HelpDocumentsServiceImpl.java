package com.example.devopsproj.service.implementations;

import com.example.devopsproj.dto.responseDto.HelpDocumentsDTO;
import com.example.devopsproj.model.HelpDocuments;
import com.example.devopsproj.model.Project;
import com.example.devopsproj.repository.HelpDocumentsRepository;
import com.example.devopsproj.repository.ProjectRepository;
import com.example.devopsproj.service.interfaces.HelpDocumentsService;
import com.example.devopsproj.service.interfaces.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HelpDocumentsServiceImpl implements HelpDocumentsService {

    private final ProjectRepository projectRepository;
    private final HelpDocumentsRepository helpDocumentsRepository;
    private final ProjectService projectService;

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
        return null;
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
    public void deleteDocument(Long fileId) {
        // Delete the HelpDocuments object by its ID from the repository
        helpDocumentsRepository.deleteById(fileId);
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