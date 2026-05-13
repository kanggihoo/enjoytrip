package com.enjoytrip.attraction.dto;

public class Sido {
    private int    sidoCode;
    private String sidoName;

    public Sido() {}
    public Sido(int sidoCode, String sidoName) {
        this.sidoCode = sidoCode;
        this.sidoName = sidoName;
    }

    public int getSidoCode()                { return sidoCode; }
    public void setSidoCode(int sidoCode)   { this.sidoCode = sidoCode; }

    public String getSidoName()             { return sidoName; }
    public void setSidoName(String sidoName){ this.sidoName = sidoName; }
}
