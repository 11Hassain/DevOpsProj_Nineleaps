package com.example.devopsproj.exceptions;

import lombok.*;

@Data
@ToString
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
