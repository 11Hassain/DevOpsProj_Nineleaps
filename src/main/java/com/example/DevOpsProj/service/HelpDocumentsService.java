package com.example.DevOpsProj.service;

import com.example.DevOpsProj.model.HelpDocuments;
import com.example.DevOpsProj.model.Project;
import com.example.DevOpsProj.repository.HelpDocumentsRepository;
import com.example.DevOpsProj.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class HelpDocumentsService {

    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private HelpDocumentsRepository helpDocumentsRepository;
    @Autowired
    private ProjectService projectService;

    public ResponseEntity<?> uploadFiles(long projectId, MultipartFile projectFile) throws IOException {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));
        HelpDocuments helpDocuments = new HelpDocuments();
        saveFile(helpDocuments, projectFile);
        helpDocuments.setProject(project);
        helpDocumentsRepository.save(helpDocuments);
        return ResponseEntity.ok("File uploaded successfully");
    }

    private void saveFile(HelpDocuments helpDocuments, MultipartFile file) throws IOException {
        if (file != null && !file.isEmpty()) {
            helpDocuments.setFileName(file.getOriginalFilename());
            helpDocuments.setData(file.getBytes());
        }
    }
}