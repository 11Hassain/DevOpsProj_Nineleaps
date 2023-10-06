package com.example.devopsproj.dto.responsedto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class HelpDocumentsDTO {

    @Positive(message = "helpDocumentId should be a positive number")
    private Long helpDocumentId;

    @Size(max = 50, message = "File name should not exceed 50 characters")
    private String fileName;
}