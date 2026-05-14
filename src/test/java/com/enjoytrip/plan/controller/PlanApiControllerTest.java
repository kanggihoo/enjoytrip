package com.enjoytrip.plan.controller;

import com.enjoytrip.common.exception.UnauthorizedException;
import com.enjoytrip.plan.dto.PlanDetailOrderRequest;
import com.enjoytrip.plan.service.TravelPlanService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
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

    @Test
    void updateOrderRejectsMissingSessionUser() {
        PlanApiController controller = new PlanApiController(travelPlanService);
        PlanDetailOrderRequest request = new PlanDetailOrderRequest();
        request.setVisitOrder(3);

        assertThatThrownBy(() -> controller.updateOrder(10, request, new MockHttpSession()))
                .isInstanceOf(UnauthorizedException.class);
        verify(travelPlanService, never()).changeDetailOrder(anyInt(), anyInt(), anyString());
    }
}
