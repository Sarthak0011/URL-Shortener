package com.sarthak.analyticsservice.controller;

import com.sarthak.analyticsservice.dto.AnalyticsResponse;
import com.sarthak.analyticsservice.dto.ClickCountResponse;
import com.sarthak.analyticsservice.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {
    
    private final AnalyticsService analyticsService;
    
    @GetMapping
    public ResponseEntity<java.util.List<ClickCountResponse>> getAllAnalytics() {
        log.info("Received request to get all analytics");
        return ResponseEntity.ok(analyticsService.getAllClickCounts());
    }

    @GetMapping("/{shortCode}/count")
    public ResponseEntity<ClickCountResponse> getClickCount(@PathVariable String shortCode) {
        log.info("Getting click count for: {}", shortCode);
        ClickCountResponse response = analyticsService.getClickCount(shortCode);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<AnalyticsResponse> getAnalytics(@PathVariable String shortCode) {
        log.info("Getting analytics for: {}", shortCode);
        AnalyticsResponse response = analyticsService.getAnalytics(shortCode);
        return ResponseEntity.ok(response);
    }
}
