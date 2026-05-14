package com.enjoytrip.attraction.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AttractionInfo {

    private int contentId;
    private int contentTypeId; // FK로 별도 contenttypes 관련 테이블 생성
    private String title;
    private int sidoCode;
    private int gugunCode;
    private String addr1;
    private String addr2;
    private String zipcode;
    private String tel;
    private String firstImage;
    private String firstImage2;
    private int readcount;
    private double mapx;
    private double mapy;
    private int mlevel;
    private String cat1;
    private String cat2;
    private String cat3;
    private String overview;
}
