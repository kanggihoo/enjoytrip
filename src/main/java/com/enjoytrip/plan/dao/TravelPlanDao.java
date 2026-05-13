package com.enjoytrip.plan.dao;

import com.enjoytrip.plan.dto.PlanDetail;
import com.enjoytrip.plan.dto.TravelPlan;

import java.sql.SQLException;
import java.util.List;

public interface TravelPlanDao {

    // 여행 계획 CRUD
    int insertPlan(TravelPlan plan) throws SQLException;
    TravelPlan selectPlanById(int planId) throws SQLException;
    List<TravelPlan> selectPlansByUserId(String userId) throws SQLException;
    void updatePlan(TravelPlan plan) throws SQLException;
    void deletePlan(int planId) throws SQLException;

    // 여행 계획 상세(방문지) CRUD
    void insertDetail(PlanDetail detail) throws SQLException;
    List<PlanDetail> selectDetailsByPlanId(int planId) throws SQLException;
    void deleteDetailsByPlanId(int planId) throws SQLException;
    void updateDetailOrder(int detailId, int visitOrder) throws SQLException;
}
