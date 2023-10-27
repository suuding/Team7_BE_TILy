package com.example.tily._core.errors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ExceptionCode {
    
    private final HttpStatus httpStatus;
    private final String message;
}
