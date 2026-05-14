package com.enjoytrip.attraction.controller;

import com.enjoytrip.attraction.client.TourApiClient;
import com.enjoytrip.attraction.dto.AttractionSearchRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/attractions")
public class AttractionApiController {

    private final TourApiClient tourApiClient;

    public AttractionApiController(TourApiClient tourApiClient) {
        this.tourApiClient = tourApiClient;
    }

    @GetMapping("/sidos")
    public ResponseEntity<String> sidos() {
        return json(tourApiClient.getSidos());
    }

    @GetMapping("/guguns")
    public ResponseEntity<String> guguns(@RequestParam Integer sidoCode) {
        return json(tourApiClient.getGuguns(sidoCode));
    }

    @GetMapping("/search")
    public ResponseEntity<String> search(@ModelAttribute AttractionSearchRequest request) {
        return json(tourApiClient.search(request));
    }

    @GetMapping("/{contentId}")
    public ResponseEntity<String> detail(@PathVariable Integer contentId) {
        return json(tourApiClient.detail(contentId));
    }

    private ResponseEntity<String> json(String body) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(body);
    }
}
