package com.exAmple.DevOpsProj.dto.responseDto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class HelpDocumentsDTO {
    private Long helpDocumentId;
    private String fileName;
}