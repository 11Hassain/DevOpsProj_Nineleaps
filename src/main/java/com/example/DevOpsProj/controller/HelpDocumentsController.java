package com.example.DevOpsProj.controller;

import com.example.DevOpsProj.dto.responseDto.HelpDocumentsDTO;
import com.example.DevOpsProj.model.HelpDocuments;
import com.example.DevOpsProj.repository.HelpDocumentsRepository;
import com.example.DevOpsProj.service.HelpDocumentsService;
import com.example.DevOpsProj.service.JwtService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@RequestMapping("api/projects")
public class HelpDocumentsController {

    @Autowired
    private HelpDocumentsService helpDocumentsService;
    @Autowired
    private HelpDocumentsRepository helpDocumentsRepository;
    @Autowired
    private JwtService jwtService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(
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
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Token");
        }
    }


    @GetMapping("/files")
    public ResponseEntity<?> getPdfFilesList(@RequestParam("projectId") long projectId,
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
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Token");
        }
    }


    @GetMapping("/files/{fileName}")
    public ResponseEntity<?> downloadPdfFile(@PathVariable("fileName") String fileName) {
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
    }
<<<<<<< HEAD

=======
>>>>>>> c66d5cae5148ffa4a0b83af05c7baf47f0d56665
    @DeleteMapping("/files/{fileId}")
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
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
    }
<<<<<<< HEAD

=======
>>>>>>> c66d5cae5148ffa4a0b83af05c7baf47f0d56665
}
