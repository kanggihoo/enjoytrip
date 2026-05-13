package com.enjoytrip.plan.service;

import com.enjoytrip.plan.dao.TravelPlanDao;
import com.enjoytrip.plan.dao.TravelPlanDaoImpl;
import com.enjoytrip.plan.dto.PlanDetail;
import com.enjoytrip.plan.dto.TravelPlan;

import java.sql.SQLException;
import java.util.List;

public class TravelPlanServiceImpl implements TravelPlanService {

    private final TravelPlanDao dao = new TravelPlanDaoImpl();

    @Override
    public int createPlan(TravelPlan plan) throws SQLException {
        return dao.insertPlan(plan);
    }

    @Override
    public TravelPlan getPlanById(int planId) throws SQLException {
        TravelPlan plan = dao.selectPlanById(planId);
        if (plan != null) {
            plan.setDetails(dao.selectDetailsByPlanId(planId));
        }
        return plan;
    }

    @Override
    public List<TravelPlan> getPlansByUserId(String userId) throws SQLException {
        return dao.selectPlansByUserId(userId);
    }

    @Override
    public void modifyPlan(TravelPlan plan) throws SQLException {
        dao.updatePlan(plan);
    }

    @Override
    public void removePlan(int planId) throws SQLException {
        dao.deletePlan(planId);
    }

    @Override
    public void addDetail(PlanDetail detail) throws SQLException {
        dao.insertDetail(detail);
    }

    @Override
    public List<PlanDetail> getDetailsByPlanId(int planId) throws SQLException {
        return dao.selectDetailsByPlanId(planId);
    }

    @Override
    public void replaceDetails(int planId, List<PlanDetail> details) throws SQLException {
        dao.deleteDetailsByPlanId(planId);
        for (int i = 0; i < details.size(); i++) {
            PlanDetail d = details.get(i);
            d.setPlanId(planId);
            d.setVisitOrder(i + 1);
            dao.insertDetail(d);
        }
    }

    @Override
    public void changeDetailOrder(int detailId, int visitOrder) throws SQLException {
        dao.updateDetailOrder(detailId, visitOrder);
    }
}
