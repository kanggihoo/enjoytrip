package com.enjoytrip.plan.service;

import com.enjoytrip.plan.dto.PlanDetail;
import com.enjoytrip.plan.dto.TravelPlan;

import java.sql.SQLException;
import java.util.List;

public interface TravelPlanService {

    int createPlan(TravelPlan plan) throws SQLException;
    TravelPlan getPlanById(int planId) throws SQLException;
    List<TravelPlan> getPlansByUserId(String userId) throws SQLException;
    void modifyPlan(TravelPlan plan) throws SQLException;
    void removePlan(int planId) throws SQLException;

    void addDetail(PlanDetail detail) throws SQLException;
    List<PlanDetail> getDetailsByPlanId(int planId) throws SQLException;
    void replaceDetails(int planId, List<PlanDetail> details) throws SQLException;
    void changeDetailOrder(int detailId, int visitOrder) throws SQLException;
}
