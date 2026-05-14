package com.enjoytrip.plan.mapper;

import com.enjoytrip.plan.dto.PlanDetail;
import com.enjoytrip.plan.dto.TravelPlan;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TravelPlanMapper {
    int insertPlan(TravelPlan plan);

    TravelPlan selectPlanById(int planId);

    List<TravelPlan> selectPlansByUserId(String userId);

    void updatePlan(TravelPlan plan);

    void deletePlan(int planId);

    void insertDetail(PlanDetail detail);

    PlanDetail selectDetailById(int detailId);

    List<PlanDetail> selectDetailsByPlanId(int planId);

    void deleteDetailsByPlanId(int planId);

    void updateDetailOrder(@Param("detailId") int detailId, @Param("visitOrder") int visitOrder);
}
