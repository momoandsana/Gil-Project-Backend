package com.web.gilproject.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
/**
 * Enum(열거형)은 서로 관련된 상수들을 정의하여 편리하게 사용하기 위한 자료형이다.
 * https://jddng.tistory.com/305
 *
 * */
public enum PathErrorCode { //enum은 'Enumeration' 의 약자로 열거, 목록 이라는 뜻

    NOTFOUND_USERID(HttpStatus.NOT_FOUND, "Not Found User Id","ID를 다시 확인하세요."),
    NOTFOUND_PATH( HttpStatus.BAD_REQUEST, "Not Found Path By User Id","해당 Id에 경로를 찾을 수 없습니다."),

    IMAGE_TRANS_FAILED( HttpStatus.BAD_REQUEST, "Image Trans Update fail","이미지 변환에 실패했습니다."),
    PIN_UPDATE_FAILED( HttpStatus.BAD_REQUEST, "Pin Update fail","핀을 수정할수 없습니다."),
    PATH_UPDATE_FAILED( HttpStatus.BAD_REQUEST, "Path Update fail","경로를 수정할수 없습니다."),
    DELETE_FAILED( HttpStatus.BAD_REQUEST, "Delete fail","삭제할 수 없습니다.");

    private final HttpStatus httpStatus;
    private  final String title;
    private final String message;
}


