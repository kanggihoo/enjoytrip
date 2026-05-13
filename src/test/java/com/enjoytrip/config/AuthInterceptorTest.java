package com.enjoytrip.config;

import com.enjoytrip.common.exception.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.springframework.web.method.HandlerMethod;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AuthInterceptorTest {

    private final AuthInterceptor interceptor = new AuthInterceptor();

    @Test
    void allowsWhenSessionHasLoginUser() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("loginUser")).thenReturn("ssafy");

        boolean result = interceptor.preHandle(request, response, mock(HandlerMethod.class));

        assertThat(result).isTrue();
    }

    @Test
    void throwsUnauthorizedWhenSessionMissing() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(request.getSession(false)).thenReturn(null);

        assertThatThrownBy(() -> interceptor.preHandle(request, response, mock(HandlerMethod.class)))
                .isInstanceOf(UnauthorizedException.class);
    }
}
