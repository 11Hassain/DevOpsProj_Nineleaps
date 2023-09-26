package com.example.devopsproj.dto.responsedto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

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
