package com.example.DevOpsProj.service;

import com.example.DevOpsProj.model.FileData;
import com.example.DevOpsProj.repository.StorageRepository;
import com.example.DevOpsProj.utils.FileStorageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

@Service
public class StorageService {

    @Autowired
    private StorageRepository storageRepository;

    public String uploadFile(MultipartFile file) throws IOException {
        FileData fileData = storageRepository.save(FileData.builder()
                .name(file.getName())
                .type(file.getContentType())
                .fileData(FileStorageUtils.compressFile(file.getBytes())).build());

        System.out.println(file.getOriginalFilename());
        if(fileData!=null){
            return "File uploaded successfully.";
        }
        return null;
    }

    public String getContentType(Long fileId) {
        Optional<FileData> fileById = storageRepository.findFileById(fileId);
        String fileName = fileById.get().getType();
        if (fileName.endsWith("pdf")) {
            return "application/pdf";
        } else if (fileName.endsWith("doc") || fileName.endsWith("docx")) {
            return "application/msword";
        } else if (fileName.endsWith("txt")) {
            return "text/plain";
        } else {
            return "image/png";
        }
    }

    public byte[] downloadFile(Long fileId){
        Optional<FileData> dbFileData = storageRepository.findFileById(fileId);
        byte[] files = FileStorageUtils.decompressFile(dbFileData.get().getFileData());
        return files;
    }



}
