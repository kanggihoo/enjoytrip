package com.enjoytrip.plan.dto;

import java.util.List;

public class TravelPlan {

    private int    planId;
    private String userId;
    private String title;
    private String startDate;
    private String endDate;
    private int    totalBudget;
    private String memo;
    private String createdAt;
    private String updatedAt;
    private List<PlanDetail> details;

    public TravelPlan() {}

    public TravelPlan(int planId, String userId, String title,
                      String startDate, String endDate,
                      int totalBudget, String memo) {
        this.planId      = planId;
        this.userId      = userId;
        this.title       = title;
        this.startDate   = startDate;
        this.endDate     = endDate;
        this.totalBudget = totalBudget;
        this.memo        = memo;
    }

    public int getPlanId()              { return planId; }
    public void setPlanId(int planId)   { this.planId = planId; }

    public String getUserId()                 { return userId; }
    public void setUserId(String userId)      { this.userId = userId; }

    public String getTitle()                  { return title; }
    public void setTitle(String title)        { this.title = title; }

    public String getStartDate()              { return startDate; }
    public void setStartDate(String startDate){ this.startDate = startDate; }

    public String getEndDate()                { return endDate; }
    public void setEndDate(String endDate)    { this.endDate = endDate; }

    public int getTotalBudget()               { return totalBudget; }
    public void setTotalBudget(int totalBudget){ this.totalBudget = totalBudget; }

    public String getMemo()                   { return memo; }
    public void setMemo(String memo)          { this.memo = memo; }

    public String getCreatedAt()              { return createdAt; }
    public void setCreatedAt(String createdAt){ this.createdAt = createdAt; }

    public String getUpdatedAt()              { return updatedAt; }
    public void setUpdatedAt(String updatedAt){ this.updatedAt = updatedAt; }

    public List<PlanDetail> getDetails()              { return details; }
    public void setDetails(List<PlanDetail> details)  { this.details = details; }

    @Override
    public String toString() {
        return "TravelPlan{planId=" + planId + ", userId='" + userId +
               "', title='" + title + "'}";
    }
}
