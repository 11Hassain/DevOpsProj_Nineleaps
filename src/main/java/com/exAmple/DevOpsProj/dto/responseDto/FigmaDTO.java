package com.exAmple.DevOpsProj.dto.responseDto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class FigmaDTO {
    private ProjectDTO projectDTO;
    @Getter
    private String figmaURL;
    private String screenshotImage;
    private String user;
    private Long figmaId;

    public FigmaDTO(String figmaURL) {
        this.figmaURL = figmaURL;
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
