package com.enjoytrip.plan.service;

import com.enjoytrip.common.exception.ErrorCode;
import com.enjoytrip.common.exception.ForbiddenException;
import com.enjoytrip.common.exception.NotFoundException;
import com.enjoytrip.plan.dto.PlanDetail;
import com.enjoytrip.plan.dto.TravelPlan;
import com.enjoytrip.plan.mapper.TravelPlanMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class TravelPlanServiceImpl implements TravelPlanService {

    private final TravelPlanMapper mapper;

    public TravelPlanServiceImpl(TravelPlanMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public int createPlan(TravelPlan plan) {
        mapper.insertPlan(plan);
        return plan.getPlanId();
    }

    @Override
    @Transactional
    public int createPlanWithDetails(TravelPlan plan, List<PlanDetail> details, String userId) {
        plan.setUserId(userId);
        mapper.insertPlan(plan);
        int planId = plan.getPlanId();
        for (int i = 0; i < details.size(); i++) {
            PlanDetail detail = details.get(i);
            detail.setPlanId(planId);
            detail.setVisitOrder(i + 1);
            mapper.insertDetail(detail);
        }
        return planId;
    }

    @Override
    public TravelPlan getPlanById(int planId) {
        TravelPlan plan = mapper.selectPlanById(planId);
        if (plan != null) {
            plan.setDetails(mapper.selectDetailsByPlanId(planId));
        }
        return plan;
    }

    @Override
    public List<TravelPlan> getPlansByUserId(String userId) {
        return mapper.selectPlansByUserId(userId);
    }

    @Override
    public void modifyPlan(TravelPlan plan, String userId) {
        requirePlanOwnedBy(plan.getPlanId(), userId);
        mapper.updatePlan(plan);
    }

    @Override
    public void removePlan(int planId, String userId) {
        requirePlanOwnedBy(planId, userId);
        mapper.deletePlan(planId);
    }

    @Override
    public void addDetail(PlanDetail detail, String userId) {
        requirePlanOwnedBy(detail.getPlanId(), userId);
        List<PlanDetail> existing = mapper.selectDetailsByPlanId(detail.getPlanId());
        detail.setVisitOrder(existing.size() + 1);
        mapper.insertDetail(detail);
    }

    @Override
    public List<PlanDetail> getDetailsByPlanId(int planId) {
        return mapper.selectDetailsByPlanId(planId);
    }

    @Override
    @Transactional
    public void replaceDetails(int planId, List<PlanDetail> details, String userId) {
        requirePlanOwnedBy(planId, userId);
        mapper.deleteDetailsByPlanId(planId);
        for (int i = 0; i < details.size(); i++) {
            PlanDetail d = details.get(i);
            d.setPlanId(planId);
            d.setVisitOrder(i + 1);
            mapper.insertDetail(d);
        }
    }

    @Override
    public void changeDetailOrder(int detailId, int visitOrder, String userId) {
        PlanDetail detail = mapper.selectDetailById(detailId);
        if (detail == null) {
            throw new NotFoundException(ErrorCode.NOT_FOUND);
        }
        requirePlanOwnedBy(detail.getPlanId(), userId);
        mapper.updateDetailOrder(detailId, visitOrder);
    }

    private TravelPlan requirePlanOwnedBy(int planId, String userId) {
        TravelPlan plan = mapper.selectPlanById(planId);
        if (plan == null) {
            throw new NotFoundException(ErrorCode.NOT_FOUND);
        }
        if (!Objects.equals(plan.getUserId(), userId)) {
            throw new ForbiddenException(ErrorCode.FORBIDDEN);
        }
        return plan;
    }
}
