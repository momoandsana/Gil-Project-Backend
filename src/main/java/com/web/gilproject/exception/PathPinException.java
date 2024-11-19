package com.web.gilproject.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PathPinException extends  RuntimeException{
    private final PathErrorCode pathErrorCode;
}
