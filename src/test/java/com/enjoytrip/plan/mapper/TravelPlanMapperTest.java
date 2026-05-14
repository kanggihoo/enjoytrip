package com.enjoytrip.plan.mapper;

import com.enjoytrip.plan.dto.PlanDetail;
import com.enjoytrip.plan.dto.TravelPlan;
import com.enjoytrip.support.AbstractMySqlContainerTest;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@MybatisTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TravelPlanMapperTest extends AbstractMySqlContainerTest {

    @Autowired
    TravelPlanMapper travelPlanMapper;

    @Test
    void insertPlanDetailUpdateOrderAndDeletePlan() {
        TravelPlan plan = new TravelPlan();
        plan.setUserId("ssafy");
        plan.setTitle("Mapper plan");
        plan.setStartDate("2026-06-01");
        plan.setEndDate("2026-06-03");
        plan.setTotalBudget(300000);
        plan.setMemo("Mapper memo");

        travelPlanMapper.insertPlan(plan);

        assertThat(plan.getPlanId()).isPositive();
        TravelPlan insertedPlan = travelPlanMapper.selectPlanById(plan.getPlanId());
        assertThat(insertedPlan.getTitle()).isEqualTo("Mapper plan");

        PlanDetail second = detail(plan.getPlanId(), "Second stop", 2);
        PlanDetail first = detail(plan.getPlanId(), "First stop", 1);
        travelPlanMapper.insertDetail(second);
        travelPlanMapper.insertDetail(first);

        assertThat(first.getDetailId()).isPositive();
        assertThat(second.getDetailId()).isPositive();

        List<PlanDetail> details = travelPlanMapper.selectDetailsByPlanId(plan.getPlanId());
        assertThat(details)
                .extracting(PlanDetail::getTitle)
                .containsExactly("First stop", "Second stop");

        travelPlanMapper.updateDetailOrder(second.getDetailId(), 0);

        List<PlanDetail> reordered = travelPlanMapper.selectDetailsByPlanId(plan.getPlanId());
        assertThat(reordered)
                .extracting(PlanDetail::getTitle)
                .containsExactly("Second stop", "First stop");

        travelPlanMapper.deletePlan(plan.getPlanId());

        assertThat(travelPlanMapper.selectPlanById(plan.getPlanId())).isNull();
        assertThat(travelPlanMapper.selectDetailsByPlanId(plan.getPlanId())).isEmpty();
    }

    private PlanDetail detail(int planId, String title, int visitOrder) {
        return new PlanDetail(planId, 0, title, 37.5665, 126.9780, visitOrder, "2026-06-01", "memo");
    }
}
