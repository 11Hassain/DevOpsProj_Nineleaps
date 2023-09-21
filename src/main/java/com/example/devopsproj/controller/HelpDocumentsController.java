package com.example.devopsproj.controller;

import com.example.devopsproj.dto.responseDto.HelpDocumentsDTO;
import com.example.devopsproj.model.HelpDocuments;
import com.example.devopsproj.repository.HelpDocumentsRepository;
import com.example.devopsproj.service.interfaces.HelpDocumentsService;
import com.example.devopsproj.service.interfaces.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/helpdocuments")
@RequiredArgsConstructor
public class HelpDocumentsController {
    private final HelpDocumentsService helpDocumentsService;
    private final HelpDocumentsRepository helpDocumentsRepository;
    private final JwtService jwtService;

    private static final String INVALID_TOKEN = "Invalid Token";

    // Upload a file associated with a specific project.
    @PostMapping("/upload")
    public ResponseEntity<Object> uploadFile(
            @RequestParam("projectId") long projectId,
            @RequestParam(name = "projectFile", required = false) MultipartFile projectFile,
            @RequestHeader("AccessToken") String accessToken)
            throws IOException {
        // Check if the provided access token is valid.
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            try {
                // Determine the file extension and delegate the file upload to the service.
                String fileExtension = helpDocumentsService.getFileExtension(projectFile);
                return helpDocumentsService.uploadFiles(projectId, projectFile, fileExtension);
            } catch (IllegalArgumentException e) {
                // Handle invalid parameters with a bad request response.
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid parameters");
            }
        } else {
            // Respond with an unauthorized status if the access token is invalid.
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
        }
    }



    // Get a list of PDF files associated with a specific project.
    @GetMapping("/files")
    public ResponseEntity<Object> getPdfFilesList(@RequestParam("projectId") long projectId,
                                                  @RequestHeader("AccessToken") String accessToken) {
        // Check if the provided access token is valid.
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            // Retrieve PDF files from the repository and filter them by the specified project ID.
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
        } else {
            // Respond with an unauthorized status if the access token is invalid.
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
        }
    }

    // Download a PDF file by its file name.
    @GetMapping("/files/{fileName}")
    public ResponseEntity<Object> downloadPdfFile(@PathVariable("fileName") String fileName,
                                                  @RequestHeader("AccessToken") String accessToken) {
        // Check if the provided access token is valid.
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            // Retrieve the PDF file by its file name from the repository.
            HelpDocuments pdfFile = helpDocumentsRepository.findByFileName(fileName);
            if (pdfFile == null) {
                return ResponseEntity.notFound().build();
            }
            // Set headers for downloading the file as an attachment.
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", fileName);
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfFile.getData());
        } else {
            // Respond with an unauthorized status if the access token is invalid.
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
        }
    }

    // Delete a document by its file ID.
    @DeleteMapping("/files/{fileId}")
    public ResponseEntity<String> deleteFile(@PathVariable("fileId") Long fileId,
                                             @RequestHeader("AccessToken") String accessToken) {
        // Check if the provided access token is valid.
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            // Attempt to retrieve the document by its ID and delete it if found.
            Optional<HelpDocumentsDTO> helpDocumentsDTO = helpDocumentsService.getDocumentById(fileId);
            if (helpDocumentsDTO.isPresent()) {
                helpDocumentsService.deleteDocument(fileId);
                return ResponseEntity.ok("Document deleted successfully");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Document not found");
            }
        } else {
            // Respond with an unauthorized status if the access token is invalid.
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
        }
    }
}