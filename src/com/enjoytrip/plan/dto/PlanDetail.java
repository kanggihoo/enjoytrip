package com.enjoytrip.plan.dto;

public class PlanDetail {

    private int    detailId;
    private int    planId;
    private int    contentId;
    private String title;
    private double latitude;
    private double longitude;
    private int    visitOrder;
    private String visitDate;
    private String memo;

    public PlanDetail() {}

    public PlanDetail(int planId, int contentId, String title,
                      double latitude, double longitude,
                      int visitOrder, String visitDate, String memo) {
        this.planId      = planId;
        this.contentId   = contentId;
        this.title       = title;
        this.latitude    = latitude;
        this.longitude   = longitude;
        this.visitOrder  = visitOrder;
        this.visitDate   = visitDate;
        this.memo        = memo;
    }

    public int getDetailId()                 { return detailId; }
    public void setDetailId(int detailId)    { this.detailId = detailId; }

    public int getPlanId()                   { return planId; }
    public void setPlanId(int planId)        { this.planId = planId; }

    public int getContentId()                { return contentId; }
    public void setContentId(int contentId)  { this.contentId = contentId; }

    public String getTitle()                 { return title; }
    public void setTitle(String title)       { this.title = title; }

    public double getLatitude()              { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude()             { return longitude; }
    public void setLongitude(double longitude){ this.longitude = longitude; }

    public int getVisitOrder()               { return visitOrder; }
    public void setVisitOrder(int visitOrder){ this.visitOrder = visitOrder; }

    public String getVisitDate()             { return visitDate; }
    public void setVisitDate(String visitDate){ this.visitDate = visitDate; }

    public String getMemo()                  { return memo; }
    public void setMemo(String memo)         { this.memo = memo; }
}
