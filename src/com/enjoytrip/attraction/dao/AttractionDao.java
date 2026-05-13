package com.enjoytrip.attraction.dao;

import com.enjoytrip.attraction.dto.AttractionInfo;
import com.enjoytrip.attraction.dto.Gugun;
import com.enjoytrip.attraction.dto.Sido;

import java.sql.SQLException;
import java.util.List;

public interface AttractionDao {
    List<Sido> selectAllSido() throws SQLException;
    List<Gugun> selectGugunBySido(int sidoCode) throws SQLException;
    List<AttractionInfo> selectAttractions(int sidoCode, int gugunCode, int contentTypeId, String keyword) throws SQLException;
    AttractionInfo selectAttractionById(int contentId) throws SQLException;
    void incrementReadcount(int contentId) throws SQLException;
}
