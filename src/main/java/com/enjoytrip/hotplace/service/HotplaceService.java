package com.enjoytrip.hotplace.service;

import com.enjoytrip.hotplace.dto.Hotplace;

import java.sql.SQLException;
import java.util.List;

public interface HotplaceService {

    void registerHotplace(Hotplace hotplace) throws SQLException;
    Hotplace getHotplaceById(int hotplaceId) throws SQLException;
    List<Hotplace> getAllHotplaces() throws SQLException;
    List<Hotplace> getHotplacesByUserId(String userId) throws SQLException;
    void modifyHotplace(Hotplace hotplace) throws SQLException;
    void removeHotplace(int hotplaceId) throws SQLException;
}
