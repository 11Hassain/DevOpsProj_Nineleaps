package com.example.devopsproj.dto.responsedto;

import lombok.*;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object (DTO) for Help Documents information.
 */
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class HelpDocumentsDTO {
    /**
     * The unique identifier of the help document.
     */
    @Positive(message = "helpDocumentId should be a positive numer")
    private Long helpDocumentId;
    /**
     * The file name of the help document.
     */
    @Size(max = 50, message = "File name should not exceed 50 characters")
    private String fileName;
}