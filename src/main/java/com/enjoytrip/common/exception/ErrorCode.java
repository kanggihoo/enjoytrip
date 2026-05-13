package com.enjoytrip.common.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "요청 파라미터가 올바르지 않습니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "요청한 리소스를 찾을 수 없습니다."),
    DUPLICATE_USER_ID(HttpStatus.CONFLICT, "이미 사용 중인 아이디입니다."),
    INVALID_LOGIN(HttpStatus.UNAUTHORIZED, "아이디 또는 비밀번호가 올바르지 않습니다."),
    EXTERNAL_API_ERROR(HttpStatus.BAD_GATEWAY, "외부 API 호출 중 오류가 발생했습니다."),
    FILE_UPLOAD_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드 중 오류가 발생했습니다."),
    DATABASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "데이터베이스 처리 중 오류가 발생했습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
