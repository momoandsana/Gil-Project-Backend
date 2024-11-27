package com.web.gilproject.exception;

import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class BoardExceptionAdvice {

    @ExceptionHandler(BoardException.class)
    public ProblemDetail handleBoardException(BoardException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(e.getBoardErrorCode().getHttpStatus());
        problemDetail.setTitle(e.getBoardErrorCode().getTitle());
        problemDetail.setDetail(e.getBoardErrorCode().getMessage());
        problemDetail.setProperty("timestamp", LocalDateTime.now());
        return problemDetail;
    }
}
