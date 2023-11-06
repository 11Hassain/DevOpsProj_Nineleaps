package com.example.devopsproj.service.interfaces;

import com.example.devopsproj.dto.responsedto.HelpDocumentsDTO;
import com.example.devopsproj.model.HelpDocuments;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

public interface HelpDocumentsService {

    ResponseEntity<Object> uploadFiles(long projectId, MultipartFile projectFile, String fileExtension) throws IOException;

    String getFileExtension(MultipartFile file);
    public ResponseEntity<byte[]> downloadPdfFile(String fileName);

    ResponseEntity<Object> getPdfFilesList(long projectId);


    Optional<HelpDocumentsDTO> getDocumentById(Long fileId);

    // Delete a document by its ID
    ResponseEntity<String> softDeleteDocument(Long fileId);

    void saveFile(HelpDocuments helpDocuments, MultipartFile file, String fileExtension) throws IOException;
}

