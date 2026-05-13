package com.enjoytrip.hotplace.service;

import com.enjoytrip.hotplace.dao.HotplaceDao;
import com.enjoytrip.hotplace.dao.HotplaceDaoImpl;
import com.enjoytrip.hotplace.dto.Hotplace;

import java.sql.SQLException;
import java.util.List;

public class HotplaceServiceImpl implements HotplaceService {

    private final HotplaceDao dao = new HotplaceDaoImpl();

    @Override
    public void registerHotplace(Hotplace hotplace) throws SQLException {
        dao.insertHotplace(hotplace);
    }

    @Override
    public Hotplace getHotplaceById(int hotplaceId) throws SQLException {
        return dao.selectHotplaceById(hotplaceId);
    }

    @Override
    public List<Hotplace> getAllHotplaces() throws SQLException {
        return dao.selectAllHotplaces();
    }

    @Override
    public List<Hotplace> getHotplacesByUserId(String userId) throws SQLException {
        return dao.selectHotplacesByUserId(userId);
    }

    @Override
    public void modifyHotplace(Hotplace hotplace) throws SQLException {
        dao.updateHotplace(hotplace);
    }

    @Override
    public void removeHotplace(int hotplaceId) throws SQLException {
        dao.deleteHotplace(hotplaceId);
    }
}
