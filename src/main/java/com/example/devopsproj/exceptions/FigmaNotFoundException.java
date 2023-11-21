package com.example.devopsproj.exceptions;
/**
 * FigmaNotFoundException is a custom runtime exception class that can be used
 * to indicate that a Figma resource could not be found.
 * It extends the RuntimeException class and uses Lombok annotations for automatic
 * generation of getters and setters.
 */
import lombok.*;
@Setter
@Getter
public class FigmaNotFoundException extends RuntimeException {
    public FigmaNotFoundException(String message) {
        super(message);
    }

    public FigmaNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
