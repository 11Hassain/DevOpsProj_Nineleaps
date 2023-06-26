package com.example.DevOpsProj.controller;

import com.example.DevOpsProj.model.HelpDocuments;
import com.example.DevOpsProj.repository.HelpDocumentsRepository;
import com.example.DevOpsProj.service.HelpDocumentsService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
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

    @PostMapping("/upload")
    public ResponseEntity<?> uploadPDF(
            @RequestParam("projectId") long projectId,
            @RequestParam(name = "projectFile", required = false) MultipartFile projectFile)
            throws IOException {
        try {
            return helpDocumentsService.uploadFiles(projectId, projectFile);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid parameters");
        }
    }

    @GetMapping("/files")
    public void downloadAllPdfFiles(@RequestParam("projectId") long projectId,
                                    HttpServletResponse response) {
        List<HelpDocuments> pdfFiles = helpDocumentsRepository.findAll();
        if (pdfFiles.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            return;
        }
        List<HelpDocuments> desiredDocuments = pdfFiles.stream()
                .filter(pdfFile -> pdfFile != null && pdfFile.getProject() != null && pdfFile.getProject().getProjectId() == projectId)
                .filter(Objects::nonNull) // Filter out any remaining null values
                .collect(Collectors.toList());
        response.setContentType("application/zip");
        response.setHeader("Content-Disposition", "attachment; filename=\"pdf_files.zip\"");
        try (ZipOutputStream zipOut = new ZipOutputStream(response.getOutputStream())) {
            for (HelpDocuments pdfFile : desiredDocuments) {
                String fileName = pdfFile.getFileName()+".pdf";
                ZipEntry zipEntry = new ZipEntry(fileName);
                zipOut.putNextEntry(zipEntry);
//                Object data=pdfFile.getData();
                try (InputStream inputStream = new ByteArrayInputStream(pdfFile.getData())) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        zipOut.write(buffer, 0, bytesRead);
                    }
                }
                zipOut.closeEntry();
            }
            zipOut.finish();
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
