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
        request.addParameter("service_key", "secret");
        request.addParameter("api_key", "secret");
        request.addParameter("accessToken", "secret");
        request.addParameter("refreshToken", "secret");
        request.addParameter("currentPassword", "secret");
        request.addParameter("keyword", "seoul", "busan");
        request.addParameter("tokenizerMode", "standard");

        @SuppressWarnings("unchecked")
        Map<String, String> result = (Map<String, String>) ReflectionTestUtils.invokeMethod(
                interceptor,
                "maskedParams",
                request
        );

        assertThat(result)
                .containsEntry("serviceKey", "***")
                .containsEntry("service_key", "***")
                .containsEntry("api_key", "***")
                .containsEntry("accessToken", "***")
                .containsEntry("refreshToken", "***")
                .containsEntry("currentPassword", "***")
                .containsEntry("keyword", "seoul,busan")
                .containsEntry("tokenizerMode", "standard");
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
