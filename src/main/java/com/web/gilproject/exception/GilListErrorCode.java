package com.web.gilproject.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum GilListErrorCode {

    NOTFOUND_LIST(HttpStatus.NOT_FOUND, "Not Found Posts", "산책길을 찾을 수 없습니다"),
    EMPTY_INPUT(HttpStatus.BAD_REQUEST, "Empty input", "검색어를 입력해 주세요."),
    EMOJI_INPUT(HttpStatus.BAD_REQUEST, "Emoji input", "이모지는 검색할 수 없습니다."),
    WRONG_LENGTH(HttpStatus.BAD_REQUEST, "Too long or Too short", "검색어는 2자 이상 30자 이하로 작성해 주세요.");

    private final HttpStatus httpStatus;
    private final String title;
    private final String message;
}
