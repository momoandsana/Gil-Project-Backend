package com.web.gilproject.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BoardException extends  RuntimeException{
    private final BoardErrorCode boardErrorCode;
}
