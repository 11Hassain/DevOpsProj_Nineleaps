package com.example.devopsproj.controller;

import com.example.devopsproj.dto.responseDto.HelpDocumentsDTO;
import com.example.devopsproj.model.HelpDocuments;
import com.example.devopsproj.service.HelpDocumentsService;
import com.example.devopsproj.service.JwtService;
import com.example.devopsproj.repository.HelpDocumentsRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
@RequestMapping("api/v1/projects")
public class HelpDocumentsController {

    @Autowired
    private HelpDocumentsService helpDocumentsService;
    @Autowired
    private HelpDocumentsRepository helpDocumentsRepository;
    @Autowired
    private JwtService jwtService;

    private static final String INVALID_TOKEN = "Invalid Token";

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
            @RequestParam(name = "projectFile", required = false) MultipartFile projectFile,
            @RequestHeader("AccessToken") String accessToken)
            throws IOException {
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            try {
                String fileExtension = helpDocumentsService.getFileExtension(projectFile);
                return helpDocumentsService.uploadFiles(projectId, projectFile, fileExtension);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid parameters");
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
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
    public ResponseEntity<Object> getPdfFilesList(@RequestParam("projectId") long projectId,
                                             @RequestHeader("AccessToken") String accessToken) {
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
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
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
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
    public ResponseEntity<Object> downloadPdfFile(@PathVariable("fileName") String fileName,
                                             @RequestHeader("AccessToken") String accessToken) {
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            HelpDocuments pdfFile = helpDocumentsRepository.findByFileName(fileName);
            if (pdfFile == null) {
                return ResponseEntity.notFound().build();
            }
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", fileName);
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfFile.getData());
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
        }
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
    public ResponseEntity<String> deleteFile(@PathVariable("fileId") Long fileId,
                                             @RequestHeader("AccessToken") String accessToken){
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if(isTokenValid){
            Optional<HelpDocumentsDTO> helpDocumentsDTO = helpDocumentsService.getDocumentById(fileId);
            if(helpDocumentsDTO.isPresent()){
                helpDocumentsService.deleteDocument(fileId);
                return ResponseEntity.ok("Document deleted successfully");
            }else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Document not found");
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN);
        }
    }

}
