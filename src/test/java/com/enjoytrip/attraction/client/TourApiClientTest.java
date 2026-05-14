package com.enjoytrip.attraction.client;

import com.enjoytrip.attraction.dto.AttractionSearchRequest;
import com.enjoytrip.common.exception.ExternalApiException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TourApiClientTest {

    private MockWebServer server;
    private TourApiClient client;

    @BeforeEach
    void setUp() throws IOException {
        server = new MockWebServer();
        server.start();
        client = new TourApiClient(
                RestClient.builder(),
                new TourApiProperties(server.url("/tour").toString(), "test-key", "ETC", "EnjoyTripTest")
        );
    }

    @AfterEach
    void tearDown() throws IOException {
        server.shutdown();
    }

    @Test
    void getSidosAppendsCommonParamsAndDefaultPaging() throws Exception {
        server.enqueue(new MockResponse().setBody("{\"ok\":true}").addHeader("Content-Type", "application/json"));

        String response = client.getSidos();

        assertThat(response).isEqualTo("{\"ok\":true}");
        RecordedRequest request = server.takeRequest();
        assertThat(request.getPath()).startsWith("/tour/areaCode2?");
        assertThat(request.getRequestUrl().queryParameter("serviceKey")).isEqualTo("test-key");
        assertThat(request.getRequestUrl().queryParameter("MobileOS")).isEqualTo("ETC");
        assertThat(request.getRequestUrl().queryParameter("MobileApp")).isEqualTo("EnjoyTripTest");
        assertThat(request.getRequestUrl().queryParameter("_type")).isEqualTo("json");
        assertThat(request.getRequestUrl().queryParameter("numOfRows")).isEqualTo("50");
        assertThat(request.getRequestUrl().queryParameter("pageNo")).isEqualTo("1");
    }

    @Test
    void getGugunsAppendsSidoCodeAsAreaCode() throws Exception {
        server.enqueue(new MockResponse().setBody("{\"items\":[]}").addHeader("Content-Type", "application/json"));

        client.getGuguns(32);

        RecordedRequest request = server.takeRequest();
        assertThat(request.getPath()).startsWith("/tour/areaCode2?");
        assertThat(request.getRequestUrl().queryParameter("areaCode")).isEqualTo("32");
        assertThat(request.getRequestUrl().queryParameter("numOfRows")).isEqualTo("100");
        assertThat(request.getRequestUrl().queryParameter("pageNo")).isEqualTo("1");
    }

    @Test
    void searchAppendsEndpointSpecificParamsAndUsesRequestPaging() throws Exception {
        server.enqueue(new MockResponse().setBody("{\"totalCount\":1}").addHeader("Content-Type", "application/json"));

        client.search(AttractionSearchRequest.builder()
                .areaCode(1)
                .sigunguCode(2)
                .contentTypeId(12)
                .keyword("한라산")
                .pageNo(3)
                .numOfRows(20)
                .build());

        RecordedRequest request = server.takeRequest();
        assertThat(request.getPath()).startsWith("/tour/areaBasedList2?");
        assertThat(request.getRequestUrl().queryParameter("areaCode")).isEqualTo("1");
        assertThat(request.getRequestUrl().queryParameter("sigunguCode")).isEqualTo("2");
        assertThat(request.getRequestUrl().queryParameter("contentTypeId")).isEqualTo("12");
        assertThat(request.getRequestUrl().queryParameter("keyword")).isEqualTo("한라산");
        assertThat(request.getRequestUrl().queryParameter("pageNo")).isEqualTo("3");
        assertThat(request.getRequestUrl().queryParameter("numOfRows")).isEqualTo("20");
    }

    @Test
    void detailAppendsContentIdAndDefaultPaging() throws Exception {
        server.enqueue(new MockResponse().setBody("{\"contentid\":123}").addHeader("Content-Type", "application/json"));

        client.detail(123);

        RecordedRequest request = server.takeRequest();
        assertThat(request.getPath()).startsWith("/tour/detailCommon2?");
        assertThat(request.getRequestUrl().queryParameter("contentId")).isEqualTo("123");
        assertThat(request.getRequestUrl().queryParameter("numOfRows")).isEqualTo("1");
        assertThat(request.getRequestUrl().queryParameter("pageNo")).isEqualTo("1");
    }

    @Test
    void restClientFailuresBecomeExternalApiException() {
        TourApiClient failingClient = new TourApiClient(
                RestClient.builder(),
                new TourApiProperties("http://localhost:0", "test-key", "ETC", "EnjoyTripTest")
        );

        assertThatThrownBy(failingClient::getSidos)
                .isInstanceOf(ExternalApiException.class);
    }
}
