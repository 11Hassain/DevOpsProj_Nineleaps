package com.example.devopsproj.dto.responseDto;

import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class HelpDocumentsDTO {
    private Long helpDocumentId;
    private String fileName;

}