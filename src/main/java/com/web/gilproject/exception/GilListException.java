package com.web.gilproject.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class GilListException extends RuntimeException {
    public final GilListErrorCode gilListErrorCode;
}
