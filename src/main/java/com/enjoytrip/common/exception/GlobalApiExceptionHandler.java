package com.enjoytrip.common.exception;

import java.time.LocalDateTime;

import com.enjoytrip.common.response.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@Order(Ordered.LOWEST_PRECEDENCE)
@RestControllerAdvice(basePackages = "com.enjoytrip")
public class GlobalApiExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiErrorResponse> handleBusinessException(BusinessException e, HttpServletRequest request) {
        if (!ExceptionRequestMatcher.isApiRequest(request)) {
            throw e;
        }
        ErrorCode errorCode = e.getErrorCode();
        log.warn("Business exception. code={}, path={}, message={}", errorCode.name(), request.getRequestURI(), e.getMessage());
        return ResponseEntity.status(errorCode.getStatus()).body(toBody(errorCode, request));
    }

    @ExceptionHandler({
            MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class
    })
    public ResponseEntity<ApiErrorResponse> handleBadRequest(Exception e, HttpServletRequest request) throws Exception {
        if (!ExceptionRequestMatcher.isApiRequest(request)) {
            throw e;
        }
        log.warn("Bad request. path={}, message={}", request.getRequestURI(), e.getMessage());
        return ResponseEntity.badRequest().body(toBody(ErrorCode.BAD_REQUEST, request));
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ApiErrorResponse> handleDataAccess(DataAccessException e, HttpServletRequest request) {
        if (!ExceptionRequestMatcher.isApiRequest(request)) {
            throw e;
        }
        log.error("Database exception. path={}", request.getRequestURI(), e);
        return ResponseEntity.status(ErrorCode.DATABASE_ERROR.getStatus()).body(toBody(ErrorCode.DATABASE_ERROR, request));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleException(Exception e, HttpServletRequest request) throws Exception {
        if (!ExceptionRequestMatcher.isApiRequest(request)) {
            throw e;
        }
        log.error("Unhandled exception. path={}", request.getRequestURI(), e);
        return ResponseEntity.status(ErrorCode.INTERNAL_SERVER_ERROR.getStatus()).body(toBody(ErrorCode.INTERNAL_SERVER_ERROR, request));
    }

    private ApiErrorResponse toBody(ErrorCode errorCode, HttpServletRequest request) {
        return ApiErrorResponse.builder()
                .code(errorCode.name())
                .message(errorCode.getMessage())
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();
    }
}
