package com.enjoytrip.common.exception;

import com.enjoytrip.common.response.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice(basePackages = "com.enjoytrip")
public class GlobalPageExceptionHandler {

    private final GlobalApiExceptionHandler apiExceptionHandler;

    public GlobalPageExceptionHandler(GlobalApiExceptionHandler apiExceptionHandler) {
        this.apiExceptionHandler = apiExceptionHandler;
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ModelAndView handleUnauthorized(UnauthorizedException e, HttpServletRequest request) throws Exception {
        if (ExceptionRequestMatcher.isApiRequest(request)) {
            return handleApiException(e, request);
        }
        log.warn("Unauthorized page request. path={}", request.getRequestURI());
        return new ModelAndView("redirect:/user/login");
    }

    @ExceptionHandler(ForbiddenException.class)
    public ModelAndView handleForbidden(ForbiddenException e, HttpServletRequest request) throws Exception {
        if (ExceptionRequestMatcher.isApiRequest(request)) {
            return handleApiException(e, request);
        }
        log.warn("Forbidden page request. path={}", request.getRequestURI());
        return toPageModelAndView("error/403", ErrorCode.FORBIDDEN);
    }

    @ExceptionHandler(NotFoundException.class)
    public ModelAndView handleNotFound(NotFoundException e, HttpServletRequest request) throws Exception {
        if (ExceptionRequestMatcher.isApiRequest(request)) {
            return handleApiException(e, request);
        }
        log.warn("Not found page request. path={}", request.getRequestURI());
        return toPageModelAndView("error/404", ErrorCode.NOT_FOUND);
    }

    @ExceptionHandler(BusinessException.class)
    public ModelAndView handleBusinessException(BusinessException e, HttpServletRequest request) throws Exception {
        if (ExceptionRequestMatcher.isApiRequest(request)) {
            return handleApiException(e, request);
        }
        ErrorCode errorCode = e.getErrorCode();
        log.warn("Business page exception. code={}, path={}, message={}", errorCode.name(), request.getRequestURI(), e.getMessage());
        return toPageModelAndView("error/500", errorCode);
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleException(Exception e, HttpServletRequest request) throws Exception {
        if (ExceptionRequestMatcher.isApiRequest(request)) {
            return handleApiException(e, request);
        }
        log.error("Unhandled page exception. path={}", request.getRequestURI(), e);
        return toPageModelAndView("error/500", ErrorCode.INTERNAL_SERVER_ERROR);
    }

    private ModelAndView handleApiException(Exception e, HttpServletRequest request) throws Exception {
        if (e instanceof BusinessException businessException) {
            return toApiModelAndView(apiExceptionHandler.handleBusinessException(businessException, request));
        }
        if (e instanceof MethodArgumentTypeMismatchException || e instanceof MissingServletRequestParameterException) {
            return toApiModelAndView(apiExceptionHandler.handleBadRequest(e, request));
        }
        if (e instanceof DataAccessException dataAccessException) {
            return toApiModelAndView(apiExceptionHandler.handleDataAccess(dataAccessException, request));
        }
        return toApiModelAndView(apiExceptionHandler.handleException(e, request));
    }

    private ModelAndView toPageModelAndView(String viewName, ErrorCode errorCode) {
        ModelAndView modelAndView = new ModelAndView(viewName);
        modelAndView.setStatus(errorCode.getStatus());
        modelAndView.addObject("errorCode", errorCode.name());
        modelAndView.addObject("message", errorCode.getMessage());
        return modelAndView;
    }

    private ModelAndView toApiModelAndView(ResponseEntity<ApiErrorResponse> responseEntity) {
        MappingJackson2JsonView jsonView = new MappingJackson2JsonView();
        jsonView.setExtractValueFromSingleKeyModel(true);

        ModelAndView modelAndView = new ModelAndView(jsonView);
        modelAndView.setStatus(responseEntity.getStatusCode());
        modelAndView.addObject("error", responseEntity.getBody());
        return modelAndView;
    }
}
