package com.example.devopsproj.dto.responsedto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import org.hibernate.validator.constraints.URL;

@NoArgsConstructor
//@AllArgsConstructor
@Setter
@Getter
@ToString
public class FigmaDTO {
    @NotNull(message = "projectDTO cannot be null")
    private ProjectDTO projectDTO;

    @Getter
    @NotBlank(message = "figmaURL cannot be blank")
    @URL(message = "figmaURL should be a valid URL")
    private String figmaURL;

    private String screenshotImage;
    private String user;

    @Positive(message = "figmaId should be a positive number")
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
