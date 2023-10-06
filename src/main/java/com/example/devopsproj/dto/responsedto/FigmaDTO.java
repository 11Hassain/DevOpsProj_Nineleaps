package com.example.devopsproj.dto.responsedto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

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
