package com.example.DevOpsProj.service;

import com.example.DevOpsProj.dto.responseDto.HelpDocumentsDTO;
import com.example.DevOpsProj.model.HelpDocuments;
import com.example.DevOpsProj.model.Project;
import com.example.DevOpsProj.repository.HelpDocumentsRepository;
import com.example.DevOpsProj.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.swing.text.html.Option;
import java.io.IOException;
import java.sql.DataTruncation;
import java.util.Optional;

@Service
public class HelpDocumentsService {

    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private HelpDocumentsRepository helpDocumentsRepository;
    @Autowired
    private ProjectService projectService;

    public ResponseEntity<Object> uploadFiles(long projectId, MultipartFile projectFile, String fileExtension) throws IOException {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));
        HelpDocuments helpDocuments = new HelpDocuments();
        saveFile(helpDocuments, projectFile,fileExtension);
        helpDocuments.setProject(project);
        helpDocumentsRepository.save(helpDocuments);
        return ResponseEntity.ok("File uploaded successfully");
    }

    private void saveFile(HelpDocuments helpDocuments, MultipartFile file, String fileExtension) throws IOException {
        if (file != null && !file.isEmpty()) {
            helpDocuments.setFileName(file.getOriginalFilename());
            helpDocuments.setData(file.getBytes());
            helpDocuments.setFileExtension(fileExtension);
        }
    }

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

    public void deleteDocument(Long fileId) {
        helpDocumentsRepository.deleteById(fileId);
    }
}