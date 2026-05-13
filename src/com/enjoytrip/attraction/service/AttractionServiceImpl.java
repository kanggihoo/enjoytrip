package com.enjoytrip.attraction.service;

import com.enjoytrip.attraction.dao.AttractionDao;
import com.enjoytrip.attraction.dao.AttractionDaoImpl;
import com.enjoytrip.attraction.dto.AttractionInfo;
import com.enjoytrip.attraction.dto.Gugun;
import com.enjoytrip.attraction.dto.Sido;

import java.sql.SQLException;
import java.util.List;

public class AttractionServiceImpl implements AttractionService {

    private final AttractionDao dao = new AttractionDaoImpl();

    @Override
    public List<Sido> getAllSido() throws SQLException {
        return dao.selectAllSido();
    }

    @Override
    public List<Gugun> getGugunBySido(int sidoCode) throws SQLException {
        return dao.selectGugunBySido(sidoCode);
    }

    @Override
    public List<AttractionInfo> searchAttractions(int sidoCode, int gugunCode, int contentTypeId, String keyword) throws SQLException {
        return dao.selectAttractions(sidoCode, gugunCode, contentTypeId, keyword);
    }

    @Override
    public AttractionInfo getAttractionDetail(int contentId) throws SQLException {
        dao.incrementReadcount(contentId);
        return dao.selectAttractionById(contentId);
    }
}
