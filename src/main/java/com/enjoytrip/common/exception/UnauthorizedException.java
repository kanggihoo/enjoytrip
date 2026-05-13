package com.enjoytrip.common.exception;

public class UnauthorizedException extends BusinessException {

    public UnauthorizedException(ErrorCode errorCode) {
        super(errorCode);
    }
}
