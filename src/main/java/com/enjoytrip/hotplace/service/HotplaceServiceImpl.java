package com.enjoytrip.hotplace.service;

import com.enjoytrip.common.exception.ErrorCode;
import com.enjoytrip.common.exception.ForbiddenException;
import com.enjoytrip.common.exception.NotFoundException;
import com.enjoytrip.hotplace.dto.Hotplace;
import com.enjoytrip.hotplace.mapper.HotplaceMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class HotplaceServiceImpl implements HotplaceService {

    private final HotplaceMapper hotplaceMapper;

    public HotplaceServiceImpl(HotplaceMapper hotplaceMapper) {
        this.hotplaceMapper = hotplaceMapper;
    }

    @Override
    public int registerHotplace(Hotplace hotplace) {
        hotplaceMapper.insertHotplace(hotplace);
        return hotplace.getHotplaceId();
    }

    @Override
    public Hotplace getHotplaceById(int hotplaceId) {
        return requireHotplace(hotplaceId);
    }

    @Override
    public Hotplace getHotplaceForEdit(int hotplaceId, String loginUserId) {
        Hotplace hotplace = requireHotplace(hotplaceId);
        requireOwner(hotplace, loginUserId);
        return hotplace;
    }

    @Override
    public List<Hotplace> getAllHotplaces() {
        return hotplaceMapper.selectAllHotplaces();
    }

    @Override
    public List<Hotplace> getHotplacesByUserId(String userId) {
        return hotplaceMapper.selectHotplacesByUserId(userId);
    }

    @Override
    public void modifyHotplace(Hotplace hotplace, String loginUserId) {
        Hotplace saved = requireHotplace(hotplace.getHotplaceId());
        requireOwner(saved, loginUserId);
        hotplaceMapper.updateHotplace(hotplace);
    }

    @Override
    public void removeHotplace(int hotplaceId, String loginUserId) {
        Hotplace hotplace = requireHotplace(hotplaceId);
        requireOwner(hotplace, loginUserId);
        hotplaceMapper.deleteHotplace(hotplaceId);
    }

    private Hotplace requireHotplace(int hotplaceId) {
        Hotplace hotplace = hotplaceMapper.selectHotplaceById(hotplaceId);
        if (hotplace == null) {
            throw new NotFoundException(ErrorCode.NOT_FOUND);
        }
        return hotplace;
    }

    private void requireOwner(Hotplace hotplace, String loginUserId) {
        if (!Objects.equals(hotplace.getUserId(), loginUserId)) {
            throw new ForbiddenException(ErrorCode.FORBIDDEN);
        }
    }
}
