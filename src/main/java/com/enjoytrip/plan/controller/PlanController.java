package com.enjoytrip.plan.controller;

import com.enjoytrip.common.exception.BadRequestException;
import com.enjoytrip.common.exception.ErrorCode;
import com.enjoytrip.plan.dto.PlanDetail;
import com.enjoytrip.plan.dto.TravelPlan;
import com.enjoytrip.plan.service.TravelPlanService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class PlanController {

    private final TravelPlanService travelPlanService;
    private final ObjectMapper objectMapper;

    public PlanController(TravelPlanService travelPlanService, ObjectMapper objectMapper) {
        this.travelPlanService = travelPlanService;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/plan/list")
    public String list(HttpSession session, Model model) {
        String userId = (String) session.getAttribute("loginUser");
        if (userId == null) {
            return "redirect:/user/login";
        }
        model.addAttribute("plans", travelPlanService.getPlansByUserId(userId));
        return "plan/list";
    }

    @GetMapping("/plan/write")
    public String writeForm(HttpSession session) {
        if (session.getAttribute("loginUser") == null) {
            return "redirect:/user/login";
        }
        return "plan/write";
    }

    @PostMapping("/plan/regist")
    public String regist(@ModelAttribute TravelPlan plan, String detailsJson, HttpSession session) throws Exception {
        String userId = (String) session.getAttribute("loginUser");
        if (userId == null) {
            return "redirect:/user/login";
        }
        List<PlanDetail> details = parseDetails(detailsJson);
        int newPlanId = travelPlanService.createPlanWithDetails(plan, details, userId);

        return "redirect:/plan/detail?planId=" + newPlanId;
    }

    private List<PlanDetail> parseDetails(String detailsJson) {
        if (detailsJson != null && !detailsJson.trim().isEmpty() && !detailsJson.trim().equals("[]")) {
            try {
                return objectMapper.readValue(
                        detailsJson,
                        new TypeReference<List<PlanDetail>>() {
                        }
                );
            } catch (JsonProcessingException e) {
                throw new BadRequestException(ErrorCode.BAD_REQUEST);
            }
        }
        return List.of();
    }

    @GetMapping("/plan/detail")
    public String detail(int planId, Model model) {
        model.addAttribute("plan", travelPlanService.getPlanById(planId));
        return "plan/detail";
    }

    @GetMapping("/plan/modify")
    public String modifyForm(int planId, HttpSession session, Model model) {
        if (session.getAttribute("loginUser") == null) {
            return "redirect:/user/login";
        }
        model.addAttribute("plan", travelPlanService.getPlanById(planId));
        return "plan/modify";
    }

    @PostMapping("/plan/update")
    public String update(@ModelAttribute TravelPlan plan, HttpSession session) {
        String userId = (String) session.getAttribute("loginUser");
        if (userId == null) {
            return "redirect:/user/login";
        }
        travelPlanService.modifyPlan(plan, userId);
        return "redirect:/plan/detail?planId=" + plan.getPlanId();
    }

    @GetMapping("/plan/delete")
    public String delete(int planId, HttpSession session) {
        String userId = (String) session.getAttribute("loginUser");
        if (userId == null) {
            return "redirect:/user/login";
        }
        travelPlanService.removePlan(planId, userId);
        return "redirect:/plan/list";
    }

    @PostMapping("/plan/addDetail")
    public String addDetail(@ModelAttribute PlanDetail detail, HttpSession session) {
        String userId = (String) session.getAttribute("loginUser");
        if (userId == null) {
            return "redirect:/user/login";
        }
        travelPlanService.addDetail(detail, userId);
        return "redirect:/plan/detail?planId=" + detail.getPlanId();
    }
}
