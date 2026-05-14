package com.enjoytrip.attraction.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttractionSearchRequest {
    private Integer areaCode;
    private Integer sigunguCode;
    private Integer contentTypeId;
    private String keyword;
    private Integer pageNo;
    private Integer numOfRows;
}
