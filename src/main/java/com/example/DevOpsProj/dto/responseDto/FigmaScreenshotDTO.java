package com.example.DevOpsProj.dto.responseDto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class FigmaScreenshotDTO {
    private String user;
    private String screenshotImageURL;
}