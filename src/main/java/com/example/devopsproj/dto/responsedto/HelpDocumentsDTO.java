package com.example.devopsproj.dto.responsedto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * The HelpDocumentsDTO class represents a data transfer object for Help Documents information.
 * It includes details such as the unique identifier and the file name of the Help Document.
 *
 * @version 2.0
 */

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