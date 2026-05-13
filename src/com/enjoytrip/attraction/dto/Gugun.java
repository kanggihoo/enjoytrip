package com.enjoytrip.attraction.dto;

public class Gugun {
    private int    gugunCode;
    private int    sidoCode;
    private String gugunName;

    public Gugun() {}
    public Gugun(int gugunCode, int sidoCode, String gugunName) {
        this.gugunCode = gugunCode;
        this.sidoCode  = sidoCode;
        this.gugunName = gugunName;
    }

    public int getGugunCode()                 { return gugunCode; }
    public void setGugunCode(int gugunCode)   { this.gugunCode = gugunCode; }

    public int getSidoCode()                  { return sidoCode; }
    public void setSidoCode(int sidoCode)     { this.sidoCode = sidoCode; }

    public String getGugunName()              { return gugunName; }
    public void setGugunName(String gugunName){ this.gugunName = gugunName; }
}
