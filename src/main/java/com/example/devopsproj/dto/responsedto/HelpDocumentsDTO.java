package com.example.devopsproj.dto.responsedto;

import lombok.*;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;


@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class HelpDocumentsDTO {

    @Positive(message = "helpDocumentId should be a positive numer")
    private Long helpDocumentId;

    @Size(max = 50, message = "File name should not exceed 50 characters")
    private String fileName;
}