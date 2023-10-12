package com.example.devopsproj.controller;


import com.example.devopsproj.service.interfaces.HelpDocumentsService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;


@RestController
@RequestMapping("/api/v1/helpdocuments")
@RequiredArgsConstructor
public class HelpDocumentsController {
    private final HelpDocumentsService helpDocumentsService;


    // Upload a file associated with a specific project.
    @PostMapping("/upload")
    public ResponseEntity<Object> uploadFile(
            @RequestParam("projectId") long projectId,
            @RequestParam(name = "projectFile", required = false) MultipartFile projectFile) throws IOException {
        String fileExtension = helpDocumentsService.getFileExtension(projectFile);
        return helpDocumentsService.uploadFiles(projectId, projectFile, fileExtension);
    }

    // Get a list of PDF files associated with a specific project.
    @GetMapping("/files")
    public ResponseEntity<Object> getPdfFilesList(@RequestParam("projectId") long projectId) {
        return helpDocumentsService.getPdfFilesList(projectId);
    }


    // Download a PDF file by its file name.
    @GetMapping("/files/{fileName}")
    public ResponseEntity<byte[]> downloadPdfFile(@PathVariable("fileName") String fileName) {
        return helpDocumentsService.downloadPdfFile(fileName);
    }



    // Delete a document by its file ID.
    @DeleteMapping("/files/{fileId}")
    public ResponseEntity<String> deleteFile(@PathVariable("fileId") Long fileId) {
        return helpDocumentsService.deleteDocument(fileId);
    }
}
