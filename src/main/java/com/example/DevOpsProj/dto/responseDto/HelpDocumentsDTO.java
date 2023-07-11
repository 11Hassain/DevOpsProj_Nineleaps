package com.example.DevOpsProj.dto.responseDto;

import lombok.*;

import java.sql.Blob;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class HelpDocumentsDTO {
    private Long helpDocumentId;
    private String fileName;
//    private Blob data;
}