package com.enjoytrip.common.response;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ApiErrorResponse {
    private final String code;
    private final String message;
    private final String path;
    private final LocalDateTime timestamp;
}
