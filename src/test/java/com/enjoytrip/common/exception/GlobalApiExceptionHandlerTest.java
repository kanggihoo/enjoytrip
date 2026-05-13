package com.enjoytrip.common.exception;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GlobalApiExceptionHandlerTest.TestController.class)
@Import({
        GlobalApiExceptionHandler.class,
        GlobalPageExceptionHandler.class
})
class GlobalApiExceptionHandlerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void apiExceptionReturnsJsonBody() throws Exception {
        mockMvc.perform(get("/api/test/bad-request").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.message").value("요청 파라미터가 올바르지 않습니다."))
                .andExpect(jsonPath("$.path").value("/api/test/bad-request"));
    }

    @Test
    void pageForbiddenExceptionSetsHttpStatus() {
        GlobalPageExceptionHandler handler = new GlobalPageExceptionHandler();
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/page/forbidden");
        MockHttpServletResponse response = new MockHttpServletResponse();
        Model model = new ExtendedModelMap();

        String viewName = handler.handleForbidden(
                new ForbiddenException(ErrorCode.FORBIDDEN),
                model,
                request,
                response
        );

        org.assertj.core.api.Assertions.assertThat(viewName).isEqualTo("error/403");
        org.assertj.core.api.Assertions.assertThat(response.getStatus()).isEqualTo(HttpStatus.FORBIDDEN.value());
        org.assertj.core.api.Assertions.assertThat(model.getAttribute("errorCode")).isEqualTo("FORBIDDEN");
    }

    @Test
    void pageNotFoundExceptionSetsHttpStatus() {
        GlobalPageExceptionHandler handler = new GlobalPageExceptionHandler();
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/page/missing");
        MockHttpServletResponse response = new MockHttpServletResponse();
        Model model = new ExtendedModelMap();

        String viewName = handler.handleNotFound(
                new NotFoundException(ErrorCode.NOT_FOUND),
                model,
                request,
                response
        );

        org.assertj.core.api.Assertions.assertThat(viewName).isEqualTo("error/404");
        org.assertj.core.api.Assertions.assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        org.assertj.core.api.Assertions.assertThat(model.getAttribute("errorCode")).isEqualTo("NOT_FOUND");
    }

    @Test
    void pageUnhandledExceptionSetsHttpStatus() throws Exception {
        GlobalPageExceptionHandler handler = new GlobalPageExceptionHandler();
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/page/error");
        MockHttpServletResponse response = new MockHttpServletResponse();
        Model model = new ExtendedModelMap();

        String viewName = handler.handleException(
                new RuntimeException("boom"),
                model,
                request,
                response
        );

        org.assertj.core.api.Assertions.assertThat(viewName).isEqualTo("error/500");
        org.assertj.core.api.Assertions.assertThat(response.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        org.assertj.core.api.Assertions.assertThat(model.getAttribute("errorCode")).isEqualTo("INTERNAL_SERVER_ERROR");
    }

    @Controller
    static class TestController {
        @GetMapping("/api/test/bad-request")
        @ResponseBody
        String badRequest() {
            throw new BadRequestException(ErrorCode.BAD_REQUEST);
        }
    }
}
