package com.enjoytrip.attraction.service;

import com.enjoytrip.attraction.dto.AttractionInfo;
import com.enjoytrip.attraction.dto.Gugun;
import com.enjoytrip.attraction.dto.Sido;

import java.sql.SQLException;
import java.util.List;

public interface AttractionService {
    List<Sido> getAllSido() throws SQLException;
    List<Gugun> getGugunBySido(int sidoCode) throws SQLException;
    List<AttractionInfo> searchAttractions(int sidoCode, int gugunCode, int contentTypeId, String keyword) throws SQLException;
    AttractionInfo getAttractionDetail(int contentId) throws SQLException;
}
