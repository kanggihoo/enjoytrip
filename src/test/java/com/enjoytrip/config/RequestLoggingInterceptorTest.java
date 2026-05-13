package com.enjoytrip.config;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class RequestLoggingInterceptorTest {

    private final RequestLoggingInterceptor interceptor = new RequestLoggingInterceptor();

    @Test
    void maskedParamsHidesSensitiveValuesAndKeepsNormalValues() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("serviceKey", "secret");
        request.addParameter("keyword", "seoul", "busan");

        @SuppressWarnings("unchecked")
        Map<String, String> result = (Map<String, String>) ReflectionTestUtils.invokeMethod(
                interceptor,
                "maskedParams",
                request
        );

        assertThat(result)
                .containsEntry("serviceKey", "***")
                .containsEntry("keyword", "seoul,busan");
    }

    @Test
    void currentUserReturnsAnonymousWhenSessionMissing() {
        MockHttpServletRequest request = new MockHttpServletRequest();

        String result = ReflectionTestUtils.invokeMethod(interceptor, "currentUser", request);

        assertThat(result).isEqualTo("anonymous");
    }

    @Test
    void currentUserReturnsLoginUserFromSession() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.getSession().setAttribute("loginUser", "ssafy");

        String result = ReflectionTestUtils.invokeMethod(interceptor, "currentUser", request);

        assertThat(result).isEqualTo("ssafy");
    }
}
