package com.example.devopsproj.controller;


import com.example.devopsproj.service.interfaces.HelpDocumentsService;

import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;


@RestController
@RequestMapping("/api/v1/helpdocuments")
@RequiredArgsConstructor
public class HelpDocumentsController {
    private final HelpDocumentsService helpDocumentsService;

    /**
     * Upload a project file.
     *
     * @param projectId    The ID of the project associated with the uploaded file.
     * @param projectFile  The multipart file to upload.
     * @return ResponseEntity containing the result of the file upload.
     * @throws IOException if an I/O error occurs during the file upload.
     */
    @PostMapping("/upload")
    @ApiOperation("Upload a project file")
    @ResponseStatus(HttpStatus.OK) // Replace with the appropriate status code
    public ResponseEntity<Object> uploadFile(
            @RequestParam("projectId") long projectId,
            @RequestParam(name = "projectFile", required = false) MultipartFile projectFile) throws IOException {
        // Extract the file extension from the uploaded file.
        String fileExtension = helpDocumentsService.getFileExtension(projectFile);
        // Delegate the file upload to the service and return the result.
        return helpDocumentsService.uploadFiles(projectId, projectFile, fileExtension);
    }

    /**
     * Get a list of PDF files associated with a specific project.
     *
     * @param projectId The ID of the project to retrieve PDF files for.
     * @return ResponseEntity containing a list of PDF files associated with the specified project.
     */
    @GetMapping("/files")
    @ApiOperation("Get a list of PDF files associated with a specific project")
    @ResponseStatus(HttpStatus.OK) // Replace with the appropriate status code
    public ResponseEntity<Object> getPdfFilesList(@RequestParam("projectId") long projectId) {
        // Retrieve a list of PDF files associated with the specified project.
        return helpDocumentsService.getPdfFilesList(projectId);
    }

    /**
     * Download a PDF file by its file name.
     *
     * @param fileName The name of the PDF file to download.
     * @return ResponseEntity containing the PDF file content.
     */
    @GetMapping("/files/{fileName}")
    @ApiOperation("Download a PDF file by its file name")
    @ResponseStatus(HttpStatus.OK) // Replace with the appropriate status code
    public ResponseEntity<byte[]> downloadPdfFile(@PathVariable("fileName") String fileName) {
        // Download the PDF file by its given file name.
        return helpDocumentsService.downloadPdfFile(fileName);
    }

    /**
     * Soft delete a document by its file ID.
     *
     * @param fileId The ID of the document to be soft deleted.
     * @return ResponseEntity indicating the status of the soft deletion.
     */
    @DeleteMapping("/files/{fileId}")
    @ApiOperation("Soft delete a document by its file ID")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> deleteFile(@PathVariable("fileId") Long fileId) {
        return helpDocumentsService.softDeleteDocument(fileId);
    }
}
