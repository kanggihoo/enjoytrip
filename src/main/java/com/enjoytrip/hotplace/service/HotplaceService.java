package com.enjoytrip.hotplace.service;

import com.enjoytrip.hotplace.dto.Hotplace;

import java.util.List;

public interface HotplaceService {

    int registerHotplace(Hotplace hotplace);
    Hotplace getHotplaceById(int hotplaceId);
    Hotplace getHotplaceForEdit(int hotplaceId, String loginUserId);
    List<Hotplace> getAllHotplaces();
    List<Hotplace> getHotplacesByUserId(String userId);
    void modifyHotplace(Hotplace hotplace, String loginUserId);
    void removeHotplace(int hotplaceId, String loginUserId);
}
