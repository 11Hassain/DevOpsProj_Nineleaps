package com.example.devopsproj.controller;

import com.example.devopsproj.dto.responsedto.HelpDocumentsDTO;
import com.example.devopsproj.exceptions.NotFoundException;
import com.example.devopsproj.model.HelpDocuments;
import com.example.devopsproj.service.interfaces.HelpDocumentsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
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

            try {
                String fileExtension = helpDocumentsService.getFileExtension(projectFile);
                return helpDocumentsService.uploadFiles(projectId, projectFile, fileExtension);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid parameters");
            }

    }


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

            try{
                List<HelpDocumentsDTO> fileInfos = helpDocumentsService.getAllDocumentsByProjectId(projectId);
                return ResponseEntity.ok().body(fileInfos);
            }catch (NotFoundException e){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No PDF files found");
            }

    }

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

            HelpDocuments pdfFile = helpDocumentsService.getPdfFile(fileName);
            if (pdfFile == null) {
                return ResponseEntity.notFound().build();
            }
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", fileName);
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfFile.getData());

    }

    @DeleteMapping("/files/{fileId}")
    @Operation(
            description = "Delete PDF File by ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "PDF file deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "PDF file not found"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    public ResponseEntity<String> deleteFile(@PathVariable("fileId") Long fileId){

            Optional<HelpDocumentsDTO> helpDocumentsDTO = helpDocumentsService.getDocumentById(fileId);
            if(helpDocumentsDTO.isPresent()){
                helpDocumentsService.deleteDocument(fileId);
                return ResponseEntity.ok("Document deleted successfully");
            }else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Document not found");
            }

    }

}
