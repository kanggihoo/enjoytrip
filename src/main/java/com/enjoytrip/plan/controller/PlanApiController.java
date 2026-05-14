package com.enjoytrip.plan.controller;

import com.enjoytrip.common.exception.ErrorCode;
import com.enjoytrip.common.exception.UnauthorizedException;
import com.enjoytrip.plan.dto.PlanDetailOrderRequest;
import com.enjoytrip.plan.service.TravelPlanService;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class PlanApiController {

    private final TravelPlanService travelPlanService;

    public PlanApiController(TravelPlanService travelPlanService) {
        this.travelPlanService = travelPlanService;
    }

    @PatchMapping("/api/plans/details/{detailId}/order")
    public Map<String, String> updateOrder(
            @PathVariable int detailId,
            @RequestBody PlanDetailOrderRequest request,
            HttpSession session) {
        String userId = (String) session.getAttribute("loginUser");
        if (userId == null) {
            throw new UnauthorizedException(ErrorCode.UNAUTHORIZED);
        }
        travelPlanService.changeDetailOrder(detailId, request.getVisitOrder(), userId);
        return Map.of("result", "ok");
    }
}
