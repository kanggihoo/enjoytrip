package com.enjoytrip.attraction.service;

import com.enjoytrip.attraction.dto.AttractionInfo;
import com.enjoytrip.attraction.dto.Gugun;
import com.enjoytrip.attraction.dto.Sido;
import com.enjoytrip.attraction.mapper.AttractionMapper;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
public class AttractionServiceImpl implements AttractionService {

    private final AttractionMapper attractionMapper;

    public AttractionServiceImpl(AttractionMapper attractionMapper) {
        this.attractionMapper = attractionMapper;
    }

    @Override
    public List<Sido> getAllSido() throws SQLException {
        return attractionMapper.selectAllSido();
    }

    @Override
    public List<Gugun> getGugunBySido(int sidoCode) throws SQLException {
        return attractionMapper.selectGugunBySido(sidoCode);
    }

    @Override
    public List<AttractionInfo> searchAttractions(int sidoCode, int gugunCode, int contentTypeId, String keyword) throws SQLException {
        return attractionMapper.selectAttractions(sidoCode, gugunCode, contentTypeId, keyword);
    }

    @Override
    public AttractionInfo getAttractionDetail(int contentId) throws SQLException {
        attractionMapper.incrementReadcount(contentId);
        return attractionMapper.selectAttractionById(contentId);
    }
}
