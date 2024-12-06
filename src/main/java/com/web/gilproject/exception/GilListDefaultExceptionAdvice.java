package com.web.gilproject.exception;

import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GilListDefaultExceptionAdvice {
    @ExceptionHandler({GilListException.class})
    public ProblemDetail GilListExceptionHandle(GilListException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(e.getGilListErrorCode().getHttpStatus());

        problemDetail.setTitle(e.getGilListErrorCode().getTitle());
        problemDetail.setDetail(e.getGilListErrorCode().getMessage());
        problemDetail.setProperty("timestamp", LocalDateTime.now());

        return problemDetail;
    }

}
