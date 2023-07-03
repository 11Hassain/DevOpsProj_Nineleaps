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


//    @GetMapping("/files")
//    public ResponseEntity<List<HelpDocumentsDTO>> generatePdfFilesArchive(@RequestParam("projectId") long projectId,
//                                                                          @RequestHeader("AccessToken") String accessToken) {
//        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
//        if (isTokenValid) {
//            List<HelpDocuments> pdfFiles = helpDocumentsRepository.findAll();
//            List<HelpDocumentsDTO> desiredDocuments = pdfFiles.stream()
//                    .filter(pdfFile -> pdfFile != null && pdfFile.getProject() != null && pdfFile.getProject().getProjectId() == projectId)
//                    .filter(Objects::nonNull) // Filter out any remaining null values
//                    .map(pdfFile -> {
//                        HelpDocumentsDTO dto = new HelpDocumentsDTO();
//                        dto.setHelpDocumentId(pdfFile.getHelpDocumentId());
//                        dto.setFileName(pdfFile.getFileName());
//
//                        byte[] dataBytes = pdfFile.getData();
//                        Blob blobData = null;
//                        try {
//                            blobData = new SerialBlob(dataBytes);
//                        } catch (SQLException e) {
//                            // Handle the exception appropriately
//                            e.printStackTrace();
//                        }
//                        dto.setData(blobData);
//                        System.out.println(dto);
//
//                        return dto;
//                    })
//                    .collect(Collectors.toList());
//            if (desiredDocuments.isEmpty()) {
//                return ResponseEntity.notFound().build();
//            }
//            return ResponseEntity.ok(desiredDocuments);
//        } else {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
//        }
//    }



    @GetMapping("/files")
    public ResponseEntity<?> generatePdfFilesArchive(@RequestParam("projectId") long projectId,
                                                     @RequestHeader("AccessToken") String accessToken) {
        boolean isTokenValid = jwtService.isTokenTrue(accessToken);
        if (isTokenValid) {
            List<HelpDocuments> pdfFiles = helpDocumentsRepository.findAll();
            List<HelpDocuments> desiredDocuments = pdfFiles.stream()
                    .filter(pdfFile -> pdfFile != null && pdfFile.getProject() != null && pdfFile.getProject().getProjectId() == projectId)
                    .filter(Objects::nonNull) // Filter out any remaining null values
                    .collect(Collectors.toList());
            if (desiredDocuments.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                 ZipOutputStream zipOut = new ZipOutputStream(baos)) {
                for (HelpDocuments pdfFile : desiredDocuments) {
                    String fileName = pdfFile.getFileName();
                    ZipEntry zipEntry = new ZipEntry(fileName);
                    zipOut.putNextEntry(zipEntry);
                    zipOut.write(pdfFile.getData());
                    zipOut.closeEntry();
                }
                zipOut.finish();
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                headers.setContentDispositionFormData("attachment", "pdf_files.zip");
                return ResponseEntity.ok()
                        .headers(headers)
                        .body(baos.toByteArray());
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Token");
        }
    }

//    @GetMapping("/download")
//    public void downloadAllPdfFiles(@RequestParam("projectId") long projectId,
//                                    @RequestParam("category") String category,
//                                    HttpServletResponse response) {
//        List<HelpDocuments> pdfFiles = helpDocumentsRepository.findAll();
//        if (pdfFiles.isEmpty()) {
//            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
//            return;
//        }
//        List<HelpDocuments> desiredDocuments = pdfFiles.stream()
//                .filter(Objects::nonNull)
//                .filter(pdfFile -> pdfFile != null && pdfFile.getProject() != null && pdfFile.getProject().getProjectId() == projectId)
////            .filter(Objects::nonNull)
//                .collect(Collectors.toList());
//        if (desiredDocuments.isEmpty()) {
//            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
//            return;
//        }
//        response.setContentType("application/zip");
//        response.setHeader("Content-Disposition", "attachment; filename=\"pdf_files.zip\"");
//        try (ZipOutputStream zipOut = new ZipOutputStream(response.getOutputStream())) {
//            for (HelpDocuments pdfFile : desiredDocuments) {
//                String fileName = pdfFile.getFileName() + ".pdf";
//                ZipEntry zipEntry = new ZipEntry(fileName);
//                zipOut.putNextEntry(zipEntry);
//                try (InputStream inputStream = new ByteArrayInputStream(pdfFile.getData())) {
//                    byte[] buffer = new byte[4096];
//                    int bytesRead;
//                    while ((bytesRead = inputStream.read(buffer)) != -1) {
//                        zipOut.write(buffer, 0, bytesRead);
//                    }
//                }
//                zipOut.closeEntry();
//            }
//            zipOut.finish();
//        } catch (IOException e) {
//            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//        }
//    }

}
