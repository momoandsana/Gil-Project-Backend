package com.web.gilproject.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BoardException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final String title;

    public BoardException(HttpStatus httpStatus, String title, String message) {
        super(message);
        this.httpStatus = httpStatus;
        this.title = title;
    }
}
