package com.web.gilproject.exception;


import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class PathDefaultExceptionAdvice {
    @ExceptionHandler({PathPinException.class})
    public ProblemDetail PathExceptionHandle(PathPinException e){
        ProblemDetail problemDetail = ProblemDetail.forStatus(e.getPathErrorCode().getHttpStatus());

        problemDetail.setTitle(e.getPathErrorCode().getTitle());
        problemDetail.setDetail(e.getPathErrorCode().getMessage());
        problemDetail.setProperty("timestamp", LocalDateTime.now());

        return  problemDetail;
    }

}
