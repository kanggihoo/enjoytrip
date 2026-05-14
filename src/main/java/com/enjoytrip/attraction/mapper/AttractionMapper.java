package com.enjoytrip.attraction.mapper;

import com.enjoytrip.attraction.dto.AttractionInfo;
import com.enjoytrip.attraction.dto.Gugun;
import com.enjoytrip.attraction.dto.Sido;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AttractionMapper {

    List<Sido> selectAllSido();

    List<Gugun> selectGugunBySido(@Param("sidoCode") int sidoCode);

    List<AttractionInfo> selectAttractions(
            @Param("sidoCode") int sidoCode,
            @Param("gugunCode") int gugunCode,
            @Param("contentTypeId") int contentTypeId,
            @Param("keyword") String keyword
    );

    AttractionInfo selectAttractionById(@Param("contentId") int contentId);

    int incrementReadcount(@Param("contentId") int contentId);
}
