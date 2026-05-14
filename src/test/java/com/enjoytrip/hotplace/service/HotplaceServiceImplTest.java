package com.enjoytrip.hotplace.service;

import com.enjoytrip.common.exception.ForbiddenException;
import com.enjoytrip.common.exception.NotFoundException;
import com.enjoytrip.hotplace.dto.Hotplace;
import com.enjoytrip.hotplace.mapper.HotplaceMapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class HotplaceServiceImplTest {

    private final HotplaceMapper mapper = mock(HotplaceMapper.class);
    private final HotplaceService service = new HotplaceServiceImpl(mapper);

    @Test
    void modifyThrowsNotFoundWhenHotplaceMissing() {
        when(mapper.selectHotplaceById(1)).thenReturn(null);
        Hotplace hotplace = hotplace(1, null);

        assertThatThrownBy(() -> service.modifyHotplace(hotplace, "ssafy"))
                .isInstanceOf(NotFoundException.class);

        verify(mapper, never()).updateHotplace(hotplace);
    }

    @Test
    void modifyThrowsForbiddenWhenOwnerDiffers() {
        when(mapper.selectHotplaceById(1)).thenReturn(hotplace(1, "owner"));
        Hotplace hotplace = hotplace(1, null);

        assertThatThrownBy(() -> service.modifyHotplace(hotplace, "other"))
                .isInstanceOf(ForbiddenException.class);

        verify(mapper, never()).updateHotplace(hotplace);
    }

    @Test
    void modifyThrowsForbiddenWhenLoginUserIsNull() {
        when(mapper.selectHotplaceById(1)).thenReturn(hotplace(1, "owner"));
        Hotplace hotplace = hotplace(1, null);

        assertThatThrownBy(() -> service.modifyHotplace(hotplace, null))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void modifyUpdatesWhenLoginUserOwnsHotplace() {
        when(mapper.selectHotplaceById(1)).thenReturn(hotplace(1, "ssafy"));
        Hotplace hotplace = hotplace(1, null);

        service.modifyHotplace(hotplace, "ssafy");

        verify(mapper).updateHotplace(hotplace);
    }

    @Test
    void removeThrowsNotFoundWhenHotplaceMissing() {
        when(mapper.selectHotplaceById(1)).thenReturn(null);

        assertThatThrownBy(() -> service.removeHotplace(1, "ssafy"))
                .isInstanceOf(NotFoundException.class);

        verify(mapper, never()).deleteHotplace(1);
    }

    @Test
    void removeThrowsForbiddenWhenOwnerDiffers() {
        when(mapper.selectHotplaceById(1)).thenReturn(hotplace(1, "owner"));

        assertThatThrownBy(() -> service.removeHotplace(1, "other"))
                .isInstanceOf(ForbiddenException.class);

        verify(mapper, never()).deleteHotplace(1);
    }

    @Test
    void removeDeletesWhenLoginUserOwnsHotplace() {
        when(mapper.selectHotplaceById(1)).thenReturn(hotplace(1, "ssafy"));

        service.removeHotplace(1, "ssafy");

        verify(mapper).deleteHotplace(1);
    }

    @Test
    void getForEditChecksMissingHotplace() {
        when(mapper.selectHotplaceById(1)).thenReturn(null);

        assertThatThrownBy(() -> service.getHotplaceForEdit(1, "ssafy"))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getForEditChecksOwnership() {
        when(mapper.selectHotplaceById(1)).thenReturn(hotplace(1, "owner"));

        assertThatThrownBy(() -> service.getHotplaceForEdit(1, "other"))
                .isInstanceOf(ForbiddenException.class);
    }

    private Hotplace hotplace(int hotplaceId, String userId) {
        Hotplace hotplace = new Hotplace();
        hotplace.setHotplaceId(hotplaceId);
        hotplace.setUserId(userId);
        return hotplace;
    }
}
