package com.example.devopsproj.service.implementations;

import com.example.devopsproj.exceptions.NotFoundException;
import com.example.devopsproj.model.HelpDocuments;
import com.example.devopsproj.dto.responsedto.HelpDocumentsDTO;
import com.example.devopsproj.model.Project;
import com.example.devopsproj.repository.HelpDocumentsRepository;
import com.example.devopsproj.repository.ProjectRepository;
import com.example.devopsproj.service.interfaces.HelpDocumentsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * The `HelpDocumentsServiceImpl` class provides services for managing help documents associated with projects.
 * It includes methods for uploading, retrieving, listing, and deleting help documents.
 *
 * @version 2.0
 */

@Service
@RequiredArgsConstructor
public class HelpDocumentsServiceImpl implements HelpDocumentsService {

    private final ProjectRepository projectRepository;
    private final HelpDocumentsRepository helpDocumentsRepository;

    @Override
    public ResponseEntity<Object> uploadFiles(long projectId, MultipartFile projectFile, String fileExtension) throws IOException {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));
        HelpDocuments helpDocuments = new HelpDocuments();
        saveFile(helpDocuments, projectFile,fileExtension);
        helpDocuments.setProject(project);
        helpDocumentsRepository.save(helpDocuments);
        return ResponseEntity.ok("File uploaded successfully");
    }

    // Saves the contents of a help document file to a `HelpDocuments` entity.
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

    @Override
    public List<HelpDocumentsDTO> getAllDocumentsByProjectId(Long projectId){
        List<HelpDocuments> pdfFiles = helpDocumentsRepository.findAll();
        List<HelpDocumentsDTO> fileInfos = pdfFiles.stream()
                .filter(pdfFile -> pdfFile != null && pdfFile.getProject() != null && Objects.equals(pdfFile.getProject().getProjectId(), projectId))
                .map(pdfFile -> new HelpDocumentsDTO(pdfFile.getHelpDocumentId(), pdfFile.getFileName()))
                .filter(helpDoc -> helpDoc.getFileName() != null) // Filter out any remaining null file names
                .toList();
        if (fileInfos.isEmpty()) {
            throw new NotFoundException("Files Not Found");
        }
        return fileInfos;
    }

    @Override
    public Optional<HelpDocumentsDTO> getDocumentById(Long fileId){
        Optional<HelpDocuments> helpDocuments = helpDocumentsRepository.findById(fileId);
        if (helpDocuments.isPresent()){
            HelpDocuments documents = helpDocuments.get();
            HelpDocumentsDTO helpDocumentsDTO = new HelpDocumentsDTO();
            helpDocumentsDTO.setHelpDocumentId(documents.getHelpDocumentId());
            helpDocumentsDTO.setFileName(documents.getFileName());
            return Optional.of(helpDocumentsDTO);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public HelpDocuments getPdfFile(String fileName) {
        return helpDocumentsRepository.findByFileName(fileName);
    }

    @Override
    public void deleteDocument(Long fileId) {
        helpDocumentsRepository.deleteById(fileId);
    }
}