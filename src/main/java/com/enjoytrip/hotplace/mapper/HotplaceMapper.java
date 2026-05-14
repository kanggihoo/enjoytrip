package com.enjoytrip.hotplace.mapper;

import com.enjoytrip.hotplace.dto.Hotplace;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface HotplaceMapper {
    void insertHotplace(Hotplace hotplace);
    Hotplace selectHotplaceById(int hotplaceId);
    List<Hotplace> selectAllHotplaces();
    List<Hotplace> selectHotplacesByUserId(String userId);
    void updateHotplace(Hotplace hotplace);
    void deleteHotplace(int hotplaceId);
}
