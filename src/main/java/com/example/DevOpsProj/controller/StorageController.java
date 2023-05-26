package com.example.DevOpsProj.controller;

import com.example.DevOpsProj.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.springframework.data.util.TypeUtils.type;

@RestController
@RequestMapping("/file")
public class StorageController {


    @Autowired
    private StorageService storageService;

    @PostMapping
    public ResponseEntity<?> uploadFile(@RequestParam("file")MultipartFile file) throws IOException {
        String uploadFile = storageService.uploadFile(file);
        return ResponseEntity.status(HttpStatus.OK)
                .body(uploadFile);
    }

    @GetMapping("/{fileId}")
    public ResponseEntity<?> downloadFile(@PathVariable("fileId") Long fileId){
        byte[] fileData = storageService.downloadFile(fileId);
        String contentType = storageService.getContentType(fileId);
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf(contentType))
                .body(fileData);
    }
}
