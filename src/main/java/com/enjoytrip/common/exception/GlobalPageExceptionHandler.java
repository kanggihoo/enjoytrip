package com.enjoytrip.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice(basePackages = "com.enjoytrip")
public class GlobalPageExceptionHandler {

    @ExceptionHandler(UnauthorizedException.class)
    public String handleUnauthorized(UnauthorizedException e, HttpServletRequest request) {
        log.warn("Unauthorized page request. path={}", request.getRequestURI());
        return "redirect:/user/login";
    }

    @ExceptionHandler(ForbiddenException.class)
    public String handleForbidden(ForbiddenException e, Model model, HttpServletRequest request) {
        log.warn("Forbidden page request. path={}", request.getRequestURI());
        model.addAttribute("errorCode", ErrorCode.FORBIDDEN.name());
        model.addAttribute("message", ErrorCode.FORBIDDEN.getMessage());
        return "error/403";
    }

    @ExceptionHandler(NotFoundException.class)
    public String handleNotFound(NotFoundException e, Model model, HttpServletRequest request) {
        log.warn("Not found page request. path={}", request.getRequestURI());
        model.addAttribute("errorCode", ErrorCode.NOT_FOUND.name());
        model.addAttribute("message", ErrorCode.NOT_FOUND.getMessage());
        return "error/404";
    }

    @ExceptionHandler(Exception.class)
    public String handleException(Exception e, Model model, HttpServletRequest request) {
        log.error("Unhandled page exception. path={}", request.getRequestURI(), e);
        model.addAttribute("errorCode", ErrorCode.INTERNAL_SERVER_ERROR.name());
        model.addAttribute("message", ErrorCode.INTERNAL_SERVER_ERROR.getMessage());
        return "error/500";
    }
}
