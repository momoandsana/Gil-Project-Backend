package com.web.gilproject.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum BoardErrorCode {
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "Post Not Found", "요청한 게시글을 찾을 수 없습니다."),
    REPLY_NOT_FOUND(HttpStatus.NOT_FOUND, "Reply Not Found", "요청한 댓글을 찾을 수 없습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER Not Found", "요청한 회원을 찾을 수 없습니다."),
    USER_NOT_ALLOWED(HttpStatus.FORBIDDEN, "User Not Allowed", "해당 작업에 대한 권한이 없습니다."),
    PATH_NOT_FOUND(HttpStatus.NOT_FOUND, "Path Not Found", "요청한 경로를 찾을 수 없습니다."),
    IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "Image Not Found", "요청한 사진을 찾을 수 없습니다."),
    IMAGE_UPLOAD_FAILED(HttpStatus.BAD_REQUEST, "Image Upload Failed", "이미지 업로드 중 오류가 발생했습니다."),
    IMAGE_DELETE_FAILED(HttpStatus.BAD_REQUEST, "Image Delete Failed", "이미지 삭제 중 오류가 발생했습니다."),
    FILE_WRITE_FAILED(HttpStatus.BAD_REQUEST, "S3 File Upload Failed", "파일 업로드 중 오류가 발생했습니다.");
    private final HttpStatus httpStatus;
    private final String title;
    private final String message;
}
