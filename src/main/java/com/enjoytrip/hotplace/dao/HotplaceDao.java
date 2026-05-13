package com.enjoytrip.hotplace.dao;

import com.enjoytrip.hotplace.dto.Hotplace;

import java.sql.SQLException;
import java.util.List;

public interface HotplaceDao {

    void insertHotplace(Hotplace hotplace) throws SQLException;
    Hotplace selectHotplaceById(int hotplaceId) throws SQLException;
    List<Hotplace> selectAllHotplaces() throws SQLException;
    List<Hotplace> selectHotplacesByUserId(String userId) throws SQLException;
    void updateHotplace(Hotplace hotplace) throws SQLException;
    void deleteHotplace(int hotplaceId) throws SQLException;
}
