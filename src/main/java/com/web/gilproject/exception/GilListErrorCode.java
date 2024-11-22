package com.web.gilproject.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum GilListErrorCode {

    NOTFOUND_LIST(HttpStatus.NOT_FOUND, "Not Found Posts", "산책길을 찾을 수 없습니다");

    private final HttpStatus httpStatus;
    private final String title;
    private final String message;
}
