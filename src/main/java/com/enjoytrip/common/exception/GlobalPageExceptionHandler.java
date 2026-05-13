package com.enjoytrip.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@Order(Ordered.LOWEST_PRECEDENCE)
@ControllerAdvice(basePackages = "com.enjoytrip")
public class GlobalPageExceptionHandler {

    @ExceptionHandler(UnauthorizedException.class)
    public String handleUnauthorized(UnauthorizedException e, HttpServletRequest request) {
        if (isApiRequest(request)) {
            throw e;
        }
        log.warn("Unauthorized page request. path={}", request.getRequestURI());
        return "redirect:/user/login";
    }

    @ExceptionHandler(ForbiddenException.class)
    public String handleForbidden(ForbiddenException e, Model model, HttpServletRequest request, HttpServletResponse response) {
        if (isApiRequest(request)) {
            throw e;
        }
        log.warn("Forbidden page request. path={}", request.getRequestURI());
        response.setStatus(ErrorCode.FORBIDDEN.getStatus().value());
        model.addAttribute("errorCode", ErrorCode.FORBIDDEN.name());
        model.addAttribute("message", ErrorCode.FORBIDDEN.getMessage());
        return "error/403";
    }

    @ExceptionHandler(NotFoundException.class)
    public String handleNotFound(NotFoundException e, Model model, HttpServletRequest request, HttpServletResponse response) {
        if (isApiRequest(request)) {
            throw e;
        }
        log.warn("Not found page request. path={}", request.getRequestURI());
        response.setStatus(ErrorCode.NOT_FOUND.getStatus().value());
        model.addAttribute("errorCode", ErrorCode.NOT_FOUND.name());
        model.addAttribute("message", ErrorCode.NOT_FOUND.getMessage());
        return "error/404";
    }

    @ExceptionHandler(Exception.class)
    public String handleException(Exception e, Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (isApiRequest(request)) {
            throw e;
        }
        log.error("Unhandled page exception. path={}", request.getRequestURI(), e);
        response.setStatus(ErrorCode.INTERNAL_SERVER_ERROR.getStatus().value());
        model.addAttribute("errorCode", ErrorCode.INTERNAL_SERVER_ERROR.name());
        model.addAttribute("message", ErrorCode.INTERNAL_SERVER_ERROR.getMessage());
        return "error/500";
    }

    private boolean isApiRequest(HttpServletRequest request) {
        String servletPath = request.getServletPath();
        if ("/api".equals(servletPath) || servletPath.startsWith("/api/")) {
            return true;
        }

        String acceptHeader = request.getHeader("Accept");
        if (!StringUtils.hasText(acceptHeader)) {
            return false;
        }

        return MediaType.parseMediaTypes(acceptHeader).stream()
                .anyMatch(this::isJsonMediaType);
    }

    private boolean isJsonMediaType(MediaType mediaType) {
        String subtype = mediaType.getSubtype();
        return "json".equals(subtype) || subtype.endsWith("+json");
    }
}
