package com.enjoytrip.common.exception;

public class FileStorageException extends BusinessException {

    public FileStorageException(ErrorCode errorCode) {
        super(errorCode);
    }

    public FileStorageException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
