package com.enjoytrip.attraction.controller;

import com.enjoytrip.attraction.client.TourApiClient;
import com.enjoytrip.attraction.dto.AttractionSearchRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AttractionApiController.class)
class AttractionApiControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    TourApiClient tourApiClient;

    @Test
    void sidosReturnsRawJsonFromClient() throws Exception {
        when(tourApiClient.getSidos()).thenReturn("{\"sidos\":[]}");

        mockMvc.perform(get("/api/attractions/sidos"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"sidos\":[]}"));
    }

    @Test
    void gugunsPassesSidoCodeAndReturnsRawJsonFromClient() throws Exception {
        when(tourApiClient.getGuguns(1)).thenReturn("{\"guguns\":[]}");

        mockMvc.perform(get("/api/attractions/guguns").param("sidoCode", "1"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"guguns\":[]}"));

        verify(tourApiClient).getGuguns(1);
    }

    @Test
    void searchBindsQueryParamsToSearchRequest() throws Exception {
        when(tourApiClient.search(org.mockito.ArgumentMatchers.any(AttractionSearchRequest.class)))
                .thenReturn("{\"items\":[]}");

        mockMvc.perform(get("/api/attractions/search")
                        .param("areaCode", "1")
                        .param("sigunguCode", "2")
                        .param("contentTypeId", "12")
                        .param("keyword", "바다")
                        .param("pageNo", "4")
                        .param("numOfRows", "30"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"items\":[]}"));

        var requestCaptor = forClass(AttractionSearchRequest.class);
        verify(tourApiClient).search(requestCaptor.capture());
        AttractionSearchRequest request = requestCaptor.getValue();
        assertThat(request.getAreaCode()).isEqualTo(1);
        assertThat(request.getSigunguCode()).isEqualTo(2);
        assertThat(request.getContentTypeId()).isEqualTo(12);
        assertThat(request.getKeyword()).isEqualTo("바다");
        assertThat(request.getPageNo()).isEqualTo(4);
        assertThat(request.getNumOfRows()).isEqualTo(30);
    }

    @Test
    void detailPassesPathContentIdAndReturnsRawJsonFromClient() throws Exception {
        when(tourApiClient.detail(123)).thenReturn("{\"contentid\":123}");

        mockMvc.perform(get("/api/attractions/123"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"contentid\":123}"));

        verify(tourApiClient).detail(123);
    }
}
