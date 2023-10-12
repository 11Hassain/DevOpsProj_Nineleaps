package com.example.devopsproj.dto.responsedto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

/**
 * The FigmaDTO class represents a data transfer object for Figma project information.
 * It includes details such as the associated project, Figma URL, screenshot image, user, and Figma ID.
 *
 * @version 2.0
 */

@NoArgsConstructor
@Setter
@Getter
public class FigmaDTO {

    @NotNull(message = "projectDTO cannot be null")
    private ProjectDTO projectDTO;

    @Getter
    @NotBlank(message = "figmaURL cannot be blank")
    private String figmaURL;

    private String screenshotImage;
    private String user;

    @Positive(message = "figmaId should be a positive number")
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
}
