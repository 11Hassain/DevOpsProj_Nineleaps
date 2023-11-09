package com.example.devopsproj.dto.responsedto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import org.hibernate.validator.constraints.URL;

/**
 * Data Transfer Object (DTO) for Figma information.
 */
@NoArgsConstructor
@Setter
@Getter
@ToString
public class FigmaDTO {
    /**
     * The project associated with the Figma information.
     */
    @NotNull(message = "projectDTO cannot be null")
    private ProjectDTO projectDTO;

    /**
     * The Figma URL.
     */
    @Getter
    @NotBlank(message = "figmaURL cannot be blank")
    @URL(message = "figmaURL should be a valid URL")
    private String figmaURL;

    /**
     * The screenshot image.
     */
    private String screenshotImage;

    /**
     * The user associated with the Figma information.
     */
    private String user;

    /**
     * The Figma ID (a positive number).
     */
    @Positive(message = "figmaId should be a positive number")
    private Long figmaId;

    /**
     * Constructor to initialize the Figma URL.
     *
     * @param figmaURL The Figma URL.
     */
    public FigmaDTO(String figmaURL) {
        this.figmaURL = figmaURL;
    }

    /**
     * Constructor to initialize project and Figma URL.
     *
     * @param projectDTO The project associated with the Figma information.
     * @param figmaURL   The Figma URL.
     */
    public FigmaDTO(ProjectDTO projectDTO, String figmaURL) {
        this.projectDTO = projectDTO;
        this.figmaURL = figmaURL;
    }

    /**
     * Constructor to initialize Figma ID, project, and Figma URL.
     *
     * @param figmaId   The Figma ID (a positive number).
     * @param projectDTO The project associated with the Figma information.
     * @param figmaURL   The Figma URL.
     */
    public FigmaDTO(Long figmaId, ProjectDTO projectDTO, String figmaURL) {
        this.projectDTO = projectDTO;
        this.figmaURL = figmaURL;
        this.figmaId = figmaId;
    }

    /**
     * Constructor to initialize screenshot image and user.
     *
     * @param screenshotImage The screenshot image.
     * @param user            The user associated with the Figma information.
     */
    public FigmaDTO(String screenshotImage, String user) {
        this.screenshotImage = screenshotImage;
        this.user = user;
    }
}
