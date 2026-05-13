package com.enjoytrip.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;

final class ExceptionRequestMatcher {

    private ExceptionRequestMatcher() {
    }

    static boolean isApiRequest(HttpServletRequest request) {
        String servletPath = request.getServletPath();
        if ("/api".equals(servletPath) || servletPath.startsWith("/api/")) {
            return true;
        }

        String acceptHeader = request.getHeader("Accept");
        if (!StringUtils.hasText(acceptHeader)) {
            return false;
        }

        return MediaType.parseMediaTypes(acceptHeader).stream()
                .anyMatch(ExceptionRequestMatcher::isJsonMediaType);
    }

    private static boolean isJsonMediaType(MediaType mediaType) {
        String subtype = mediaType.getSubtype();
        return "json".equals(subtype) || subtype.endsWith("+json");
    }
}
