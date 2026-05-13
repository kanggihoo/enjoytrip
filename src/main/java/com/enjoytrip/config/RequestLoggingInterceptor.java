package com.enjoytrip.config;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class RequestLoggingInterceptor implements HandlerInterceptor {

    private static final String START_TIME = RequestLoggingInterceptor.class.getName() + ".START_TIME";
    private static final Set<String> SENSITIVE_KEYS = Set.of("userpw", "password", "servicekey", "apikey", "token", "jsessionid");

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        request.setAttribute(START_TIME, System.currentTimeMillis());
        log.info("[REQ] {} {} handler={} user={} params={}",
                request.getMethod(),
                request.getRequestURI(),
                handlerName(handler),
                currentUser(request),
                maskedParams(request));
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        Object start = request.getAttribute(START_TIME);
        long elapsed = start instanceof Long value ? System.currentTimeMillis() - value : -1L;
        log.info("[RES] {} {} status={} elapsedMs={}", request.getMethod(), request.getRequestURI(), response.getStatus(), elapsed);
    }

    private String handlerName(Object handler) {
        if (handler instanceof HandlerMethod method) {
            return method.getBeanType().getSimpleName() + "." + method.getMethod().getName();
        }
        return handler.getClass().getSimpleName();
    }

    private String currentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loginUser") == null) {
            return "anonymous";
        }
        return String.valueOf(session.getAttribute("loginUser"));
    }

    private Map<String, String> maskedParams(HttpServletRequest request) {
        Map<String, String> result = new LinkedHashMap<>();
        request.getParameterMap().forEach((key, values) -> {
            String normalized = key.toLowerCase(Locale.ROOT);
            String value = SENSITIVE_KEYS.contains(normalized)
                    ? "***"
                    : Arrays.stream(values).collect(Collectors.joining(","));
            result.put(key, value);
        });
        return result;
    }
}
