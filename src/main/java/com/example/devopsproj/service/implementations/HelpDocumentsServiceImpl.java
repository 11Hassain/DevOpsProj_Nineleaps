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

    @Override
    public ResponseEntity<Object> uploadFiles(long projectId, MultipartFile projectFile, String fileExtension) throws IOException {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));
        HelpDocuments helpDocuments = new HelpDocuments();
        saveFile(helpDocuments, projectFile, fileExtension);
        helpDocuments.setProject(project);
        helpDocumentsRepository.save(helpDocuments);
        return ResponseEntity.ok("File uploaded successfully");
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
    public Optional<HelpDocumentsDTO> getDocumentById(Long fileId) {
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

    @Override
    public void deleteDocument(Long fileId) {
        helpDocumentsRepository.deleteById(fileId);
    }

    private void saveFile(HelpDocuments helpDocuments, MultipartFile file, String fileExtension) throws IOException {
        if (file != null && !file.isEmpty()) {
            helpDocuments.setFileName(file.getOriginalFilename());
            helpDocuments.setData(file.getBytes());
            helpDocuments.setFileExtension(fileExtension);
        }
    }
}
