package com.enjoytrip.common.exception;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GlobalApiExceptionHandlerTest.TestController.class)
@Import(GlobalApiExceptionHandler.class)
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

    @Controller
    static class TestController {
        @GetMapping("/api/test/bad-request")
        @ResponseBody
        String badRequest() {
            throw new BadRequestException(ErrorCode.BAD_REQUEST);
        }
    }
}
