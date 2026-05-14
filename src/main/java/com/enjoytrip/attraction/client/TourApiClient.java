package com.enjoytrip.attraction.client;

import com.enjoytrip.attraction.dto.AttractionSearchRequest;
import com.enjoytrip.common.exception.ErrorCode;
import com.enjoytrip.common.exception.ExternalApiException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class TourApiClient {

    private final RestClient restClient;
    private final TourApiProperties properties;

    public TourApiClient(RestClient.Builder restClientBuilder, TourApiProperties properties) {
        this.restClient = restClientBuilder.build();
        this.properties = properties;
    }

    public String getSidos() {
        URI uri = commonUri("areaCode2")
                .queryParam("numOfRows", 50)
                .queryParam("pageNo", 1)
                .build(true)
                .toUri();
        return get(uri);
    }

    public String getGuguns(Integer sidoCode) {
        UriComponentsBuilder builder = commonUri("areaCode2")
                .queryParam("numOfRows", 100)
                .queryParam("pageNo", 1);
        append(builder, "areaCode", sidoCode);
        return get(builder.build(true).toUri());
    }

    public String search(AttractionSearchRequest request) {
        UriComponentsBuilder builder = commonUri("areaBasedList2")
                .queryParam("numOfRows", request.getNumOfRows() != null ? request.getNumOfRows() : 100)
                .queryParam("pageNo", request.getPageNo() != null ? request.getPageNo() : 1);
        append(builder, "areaCode", request.getAreaCode());
        append(builder, "sigunguCode", request.getSigunguCode());
        append(builder, "contentTypeId", request.getContentTypeId());
        append(builder, "keyword", request.getKeyword());
        return get(builder.build(true).toUri());
    }

    public String detail(Integer contentId) {
        UriComponentsBuilder builder = commonUri("detailCommon2")
                .queryParam("numOfRows", 1)
                .queryParam("pageNo", 1);
        append(builder, "contentId", contentId);
        return get(builder.build(true).toUri());
    }

    private UriComponentsBuilder commonUri(String path) {
        return UriComponentsBuilder.fromHttpUrl(properties.baseUrl())
                .pathSegment(path)
                .queryParam("serviceKey", encode(serviceKey()))
                .queryParam("MobileOS", encode(properties.mobileOs()))
                .queryParam("MobileApp", encode(properties.mobileApp()))
                .queryParam("_type", "json");
    }

    private String serviceKey() {
        // Accept a decoded or already URL-encoded portal key, then encode query values exactly once.
        try {
            return UriUtils.decode(properties.serviceKey(), StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            return properties.serviceKey();
        }
    }

    private void append(UriComponentsBuilder builder, String name, Object value) {
        if (value != null && !value.toString().isBlank()) {
            builder.queryParam(name, encode(value.toString()));
        }
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8).replace("+", "%20");
    }

    private String get(URI uri) {
        try {
            return restClient.get()
                    .uri(uri)
                    .retrieve()
                    .body(String.class);
        } catch (RestClientException e) {
            throw new ExternalApiException(ErrorCode.EXTERNAL_API_ERROR, e);
        }
    }
}
