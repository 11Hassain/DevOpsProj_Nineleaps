package com.example.DevOpsProj.dto.responseDto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class FigmaDTO {
    private ProjectDTO projectDTO;
    private String figmaURL;
    private String screenshotImage;
    private String user;
    private Long figmaId;

    public FigmaDTO(String figmaURL) {
        this.figmaURL = figmaURL;
    }


    public String getFigmaURL() {
        return figmaURL;
    }



    public void setFigmaURL(String figmaURL) {
        this.figmaURL = figmaURL;
    }

    public FigmaDTO(ProjectDTO projectDTO, String figmaURL) {
        this.projectDTO = projectDTO;
        this.figmaURL = figmaURL;
    }

    public FigmaDTO(Long figmaId, ProjectDTO projectDTO, String figmaURL) {
        this.projectDTO = projectDTO;
        this.figmaURL = figmaURL;
        this.figmaId = figmaId;
    }

    public FigmaDTO(String screenshotImage, String user) {
        this.screenshotImage = screenshotImage;
        this.user = user;
    }
}
