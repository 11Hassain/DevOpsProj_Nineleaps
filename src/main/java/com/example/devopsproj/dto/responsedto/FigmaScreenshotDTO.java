package com.example.devopsproj.dto.responsedto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * The FigmaScreenshotDTO class represents a data transfer object for Figma screenshot information.
 * It includes details such as the user and the URL of the screenshot image.
 *
 * @version 2.0
 */

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class FigmaScreenshotDTO {

    @Size(max = 255, message = "User should not exceed 255 characters")
    private String user;

    @NotBlank(message = "Screenshot Image URL cannot be blank")
    private String screenshotImageURL;
}
