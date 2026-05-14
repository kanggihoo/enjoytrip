package com.enjoytrip.hotplace.controller;

import com.enjoytrip.hotplace.dto.Hotplace;
import com.enjoytrip.hotplace.service.FileStorageService;
import com.enjoytrip.hotplace.service.HotplaceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(HotplaceController.class)
class HotplaceControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    HotplaceService hotplaceService;

    @MockBean
    FileStorageService fileStorageService;

    @Test
    void listAllowsAnonymousHotplaceListView() throws Exception {
        List<Hotplace> hotplaces = List.of(hotplace(1, "ssafy"));
        when(hotplaceService.getAllHotplaces()).thenReturn(hotplaces);

        mockMvc.perform(get("/hotplaces"))
                .andExpect(status().isOk())
                .andExpect(view().name("hotplace/list"))
                .andExpect(model().attribute("hotplaces", hotplaces));
    }

    @Test
    void detailAllowsAnonymousHotplaceDetailView() throws Exception {
        Hotplace hotplace = hotplace(1, "ssafy");
        when(hotplaceService.getHotplaceById(1)).thenReturn(hotplace);

        mockMvc.perform(get("/hotplaces/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("hotplace/detail"))
                .andExpect(model().attribute("hotplace", hotplace));
    }

    @Test
    void newFormRequiresSessionAuth() throws Exception {
        mockMvc.perform(get("/hotplaces/new"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/login"));
    }

    @Test
    void writeRequiresSessionAuth() throws Exception {
        mockMvc.perform(multipart("/hotplaces")
                        .file(new MockMultipartFile("image", new byte[0]))
                        .param("title", "title"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/login"));
    }

    @Test
    void editRequiresSessionAuth() throws Exception {
        mockMvc.perform(get("/hotplaces/1/edit"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/login"));
    }

    @Test
    void deleteRequiresSessionAuth() throws Exception {
        mockMvc.perform(post("/hotplaces/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/login"));
    }

    @Test
    void writeUsesSessionUserAndRedirectsToDetail() throws Exception {
        when(fileStorageService.store(any())).thenReturn("uploads/place.jpg");
        when(hotplaceService.registerHotplace(any(Hotplace.class))).thenReturn(7);

        mockMvc.perform(multipart("/hotplaces")
                        .file(new MockMultipartFile("image", "place.jpg", "image/jpeg", "image".getBytes()))
                        .sessionAttr("loginUser", "ssafy")
                        .param("title", "title")
                        .param("visitDate", "2026-05-14")
                        .param("placeType", "카페")
                        .param("description", "desc")
                        .param("latitude", "37.5")
                        .param("longitude", "127.0")
                        .param("userId", "attacker"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/hotplaces/7"));

        var hotplaceCaptor = forClass(Hotplace.class);
        verify(hotplaceService).registerHotplace(hotplaceCaptor.capture());
        assertThat(hotplaceCaptor.getValue().getUserId()).isEqualTo("ssafy");
        assertThat(hotplaceCaptor.getValue().getImagePath()).isEqualTo("uploads/place.jpg");
    }

    @Test
    void modifyUsesSessionUserForOwnershipAndExistingImageWhenNoUpload() throws Exception {
        Hotplace savedHotplace = hotplace(1, "ssafy");
        savedHotplace.setImagePath("uploads/server-old.jpg");
        when(hotplaceService.getHotplaceForEdit(1, "ssafy")).thenReturn(savedHotplace);
        when(fileStorageService.store(any())).thenReturn("");

        mockMvc.perform(multipart("/hotplaces/1")
                        .file(new MockMultipartFile("image", new byte[0]))
                        .sessionAttr("loginUser", "ssafy")
                        .param("title", "updated")
                        .param("existingImage", "uploads/tampered.html"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/hotplaces/1"));

        var hotplaceCaptor = forClass(Hotplace.class);
        verify(hotplaceService).getHotplaceForEdit(1, "ssafy");
        verify(hotplaceService).modifyHotplace(hotplaceCaptor.capture(), eq("ssafy"));
        assertThat(hotplaceCaptor.getValue().getImagePath()).isEqualTo("uploads/server-old.jpg");
    }

    @Test
    void deleteUsesSessionUserForOwnership() throws Exception {
        mockMvc.perform(post("/hotplaces/1/delete")
                        .sessionAttr("loginUser", "ssafy"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/hotplaces"));

        verify(hotplaceService).removeHotplace(1, "ssafy");
    }

    @Test
    void legacyRoutesRedirectToCanonicalRoutes() throws Exception {
        mockMvc.perform(get("/hotplace/list"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/hotplaces"));
        mockMvc.perform(get("/hotplace/write"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/hotplaces/new"));
        mockMvc.perform(get("/hotplace/detail").param("hotplaceId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/hotplaces/1"));
        mockMvc.perform(get("/hotplace/modify").param("hotplaceId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/hotplaces/1/edit"));
    }

    private Hotplace hotplace(int hotplaceId, String userId) {
        Hotplace hotplace = new Hotplace();
        hotplace.setHotplaceId(hotplaceId);
        hotplace.setUserId(userId);
        hotplace.setTitle("title");
        return hotplace;
    }
}
