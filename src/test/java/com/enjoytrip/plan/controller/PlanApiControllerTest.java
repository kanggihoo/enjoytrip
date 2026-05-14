package com.enjoytrip.plan.controller;

import com.enjoytrip.plan.service.TravelPlanService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PlanApiController.class)
class PlanApiControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    TravelPlanService travelPlanService;

    @Test
    void updateOrderUsesJsonBodyAndSessionUser() throws Exception {
        mockMvc.perform(patch("/api/plans/details/10/order")
                        .sessionAttr("loginUser", "ssafy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"visitOrder\":3}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("ok"));

        verify(travelPlanService).changeDetailOrder(10, 3, "ssafy");
    }
}
