package com.enjoytrip.plan.service;

import com.enjoytrip.plan.dto.PlanDetail;
import com.enjoytrip.plan.dto.TravelPlan;

import java.util.List;

public interface TravelPlanService {

    int createPlan(TravelPlan plan);
    int createPlanWithDetails(TravelPlan plan, List<PlanDetail> details, String userId);
    TravelPlan getPlanById(int planId);
    List<TravelPlan> getPlansByUserId(String userId);
    void modifyPlan(TravelPlan plan, String userId);
    void removePlan(int planId, String userId);

    void addDetail(PlanDetail detail, String userId);
    List<PlanDetail> getDetailsByPlanId(int planId);
    void replaceDetails(int planId, List<PlanDetail> details, String userId);
    void changeDetailOrder(int detailId, int visitOrder, String userId);
}
