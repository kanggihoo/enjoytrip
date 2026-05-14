package com.enjoytrip.plan.service;

import com.enjoytrip.common.exception.ForbiddenException;
import com.enjoytrip.common.exception.NotFoundException;
import com.enjoytrip.plan.dto.PlanDetail;
import com.enjoytrip.plan.dto.TravelPlan;
import com.enjoytrip.plan.mapper.TravelPlanMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TravelPlanServiceImplTest {

    private final TravelPlanMapper mapper = mock(TravelPlanMapper.class);
    private final TravelPlanService service = new TravelPlanServiceImpl(mapper);

    @Test
    void modifyThrowsNotFoundWhenPlanMissing() {
        when(mapper.selectPlanById(1)).thenReturn(null);
        TravelPlan plan = new TravelPlan();
        plan.setPlanId(1);

        assertThatThrownBy(() -> service.modifyPlan(plan, "ssafy"))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void modifyThrowsForbiddenWhenOwnerDiffers() {
        when(mapper.selectPlanById(1)).thenReturn(plan(1, "owner"));
        TravelPlan plan = new TravelPlan();
        plan.setPlanId(1);

        assertThatThrownBy(() -> service.modifyPlan(plan, "other"))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void modifyThrowsForbiddenWhenLoginUserIsNull() {
        when(mapper.selectPlanById(1)).thenReturn(plan(1, "owner"));
        TravelPlan plan = new TravelPlan();
        plan.setPlanId(1);

        assertThatThrownBy(() -> service.modifyPlan(plan, null))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void removeChecksOwnerAndDeletes() {
        when(mapper.selectPlanById(1)).thenReturn(plan(1, "ssafy"));

        service.removePlan(1, "ssafy");

        verify(mapper).deletePlan(1);
    }

    @Test
    void replaceDetailsChecksOwnerAndReordersDetails() {
        when(mapper.selectPlanById(1)).thenReturn(plan(1, "ssafy"));
        PlanDetail first = new PlanDetail();
        PlanDetail second = new PlanDetail();

        service.replaceDetails(1, List.of(first, second), "ssafy");

        assertThat(first.getPlanId()).isEqualTo(1);
        assertThat(first.getVisitOrder()).isEqualTo(1);
        assertThat(second.getPlanId()).isEqualTo(1);
        assertThat(second.getVisitOrder()).isEqualTo(2);
        verify(mapper).deleteDetailsByPlanId(1);
        verify(mapper).insertDetail(first);
        verify(mapper).insertDetail(second);
    }

    @Test
    void addDetailChecksOwnerAndAppendsNextOrder() {
        when(mapper.selectPlanById(1)).thenReturn(plan(1, "ssafy"));
        when(mapper.selectDetailsByPlanId(1)).thenReturn(List.of(new PlanDetail(), new PlanDetail()));
        PlanDetail detail = new PlanDetail();
        detail.setPlanId(1);

        service.addDetail(detail, "ssafy");

        assertThat(detail.getVisitOrder()).isEqualTo(3);
        verify(mapper).insertDetail(detail);
    }

    @Test
    void changeDetailOrderChecksOwningPlanThroughDetail() {
        PlanDetail detail = new PlanDetail();
        detail.setDetailId(10);
        detail.setPlanId(1);
        when(mapper.selectDetailById(10)).thenReturn(detail);
        when(mapper.selectPlanById(1)).thenReturn(plan(1, "ssafy"));

        service.changeDetailOrder(10, 3, "ssafy");

        verify(mapper).updateDetailOrder(10, 3);
    }

    @Test
    void changeDetailOrderThrowsNotFoundWhenDetailMissing() {
        when(mapper.selectDetailById(10)).thenReturn(null);

        assertThatThrownBy(() -> service.changeDetailOrder(10, 3, "ssafy"))
                .isInstanceOf(NotFoundException.class);

        verify(mapper, never()).updateDetailOrder(10, 3);
    }

    private TravelPlan plan(int planId, String userId) {
        TravelPlan plan = new TravelPlan();
        plan.setPlanId(planId);
        plan.setUserId(userId);
        return plan;
    }
}
