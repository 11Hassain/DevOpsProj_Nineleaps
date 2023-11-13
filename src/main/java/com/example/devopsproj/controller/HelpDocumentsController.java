package com.example.devopsproj.controller;

import com.example.devopsproj.dto.responsedto.HelpDocumentsDTO;
import com.example.devopsproj.exceptions.NotFoundException;
import com.example.devopsproj.model.HelpDocuments;
import com.example.devopsproj.service.interfaces.HelpDocumentsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * The HelpDocumentsController class manages RESTful API endpoints for handling project help documents.
 * It provides functionality for uploading, downloading, and deleting PDF files associated with a project.
 * User authentication is required using the JwtServiceImpl.
 *
 * @version 2.0
 */

@RestController
@RequestMapping("api/v1/projects")
@Validated
@RequiredArgsConstructor
public class HelpDocumentsController {

    private final HelpDocumentsService helpDocumentsService;
    private static final Logger logger = LoggerFactory.getLogger(HelpDocumentsController.class);

    /**
     * Upload a project file for a specific project.
     *
     * @param projectId    The ID of the project associated with the uploaded file.
     * @param projectFile  The project file to upload.
     * @return ResponseEntity indicating the result of the file upload.
     * @throws IOException if there's an issue with file operations.
     */
    @PostMapping("/upload")
    @Operation(
            description = "Upload Project File",
            responses = {
                    @ApiResponse(responseCode = "200", description = "File uploaded successfully"),
                    @ApiResponse(responseCode = "400", description = "Bad Request"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> uploadFile(
            @RequestParam("projectId") long projectId,
            @RequestParam(name = "projectFile", required = false) MultipartFile projectFile)
            throws IOException {

        logger.info("Received a request to upload a project file for Project ID: {}", projectId);

        try {
            String fileExtension = helpDocumentsService.getFileExtension(projectFile);
            ResponseEntity<Object> result = helpDocumentsService.uploadFiles(projectId, projectFile, fileExtension);

            if (result.getStatusCode() == HttpStatus.OK) {
                logger.info("File uploaded successfully for Project ID: {}", projectId);
            } else {
                logger.error("Failed to upload the file for Project ID: {}", projectId);
            }

            return result;
        } catch (IllegalArgumentException e) {
            logger.error("Invalid parameters for file upload request.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid parameters");
        }
    }

    /**
     * Get a list of PDF files associated with a specific project.
     *
     * @param projectId The ID of the project associated with the PDF files.
     * @return ResponseEntity with the retrieved list of PDF files or an error response if not found.
     */
    @GetMapping("/files")
    @Operation(
            description = "Get List of PDF Files",
            responses = {
                    @ApiResponse(responseCode = "200", description = "PDF files retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "No PDF files found"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> getPdfFilesList(@RequestParam("projectId") long projectId) {
        logger.info("Received a request to retrieve a list of PDF files for Project ID: {}", projectId);

        try {
            List<HelpDocumentsDTO> fileInfos = helpDocumentsService.getAllDocumentsByProjectId(projectId);
            logger.info("PDF files retrieved successfully for Project ID: {}", projectId);
            return ResponseEntity.ok().body(fileInfos);
        } catch (NotFoundException e) {
            logger.info("No PDF files found for Project ID: {}", projectId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No PDF files found");
        }
    }

    /**
     * Download a PDF file by its name.
     *
     * @param fileName The name of the PDF file to download.
     * @return ResponseEntity with the PDF file content for download or an error response if not found.
     */
    @GetMapping("/files/{fileName}")
    @Operation(
            description = "Download PDF File by Name",
            responses = {
                    @ApiResponse(responseCode = "200", description = "PDF file downloaded successfully"),
                    @ApiResponse(responseCode = "404", description = "PDF file not found"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Object> downloadPdfFile(@PathVariable("fileName") String fileName) {
        logger.info("Received a request to download a PDF file with the name: {}", fileName);

        HelpDocuments pdfFile = helpDocumentsService.getPdfFile(fileName);
        if (pdfFile == null) {
            logger.info("PDF file not found with the name: {}", fileName);
            return ResponseEntity.notFound().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", fileName);
        logger.info("PDF file {} downloaded successfully", fileName);

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfFile.getData());
    }

    /**
     * Delete a PDF file by its ID.
     *
     * @param fileId The ID of the PDF file to delete.
     * @return ResponseEntity with a success message or an error response if not found.
     */
    @DeleteMapping("/files/{fileId}")
    @Operation(
            description = "Delete PDF File by ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "PDF file deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "PDF file not found"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    public ResponseEntity<String> deleteFile(@PathVariable("fileId") Long fileId) {
        logger.info("Received a request to delete a PDF file with ID: {}", fileId);

        Optional<HelpDocumentsDTO> helpDocumentsDTO = helpDocumentsService.getDocumentById(fileId);
        if (helpDocumentsDTO.isPresent()) {
            helpDocumentsService.deleteDocument(fileId);
            logger.info("PDF file with ID {} deleted successfully", fileId);
            return ResponseEntity.ok("Document deleted successfully");
        } else {
            logger.info("PDF file not found with ID: {}", fileId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Document not found");
        }
    }

}
