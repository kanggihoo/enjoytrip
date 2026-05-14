package com.enjoytrip.plan.controller;

import com.enjoytrip.plan.service.TravelPlanService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PlanController.class)
class PlanControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    TravelPlanService travelPlanService;

    @Test
    void registRejectsBadDetailsJsonBeforeCreatingPlan() throws Exception {
        mockMvc.perform(post("/plan/regist")
                        .sessionAttr("loginUser", "ssafy")
                        .param("title", "trip")
                        .param("detailsJson", "[bad-json]"))
                .andExpect(status().isBadRequest());

        verify(travelPlanService, never()).createPlanWithDetails(any(), any(), any());
    }
}
