package com.example.devopsproj.dto.responsedto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;


/**
 * Data Transfer Object (DTO) for Figma screenshot information.
 */
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Setter
@Getter
public class FigmaScreenshotDTO {
    /**
     * The user associated with the Figma screenshot information.
     */
    @Size(max = 255, message = "User should not exceed 255 characters")
    private String user;

    /**
     * The URL of the screenshot image.
     */
    @NotBlank(message = "Screenshot Image URL cannot be blank")
    private String screenshotImageURL;
}

