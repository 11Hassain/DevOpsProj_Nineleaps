package com.example.devopsproj.service.interfaces;

import com.example.devopsproj.dto.responsedto.HelpDocumentsDTO;
import com.example.devopsproj.model.HelpDocuments;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface HelpDocumentsService {
    ResponseEntity<Object> uploadFiles(long projectId, MultipartFile projectFile, String fileExtension) throws IOException;

    void saveFile(HelpDocuments helpDocuments, MultipartFile file, String fileExtension) throws IOException;

    String getFileExtension(MultipartFile file);

    List<HelpDocumentsDTO> getAllDocumentsByProjectId(Long projectId);

    Optional<HelpDocumentsDTO> getDocumentById(Long fileId);

    HelpDocuments getPdfFile(String fileName);

    void deleteDocument(Long fileId);
}
