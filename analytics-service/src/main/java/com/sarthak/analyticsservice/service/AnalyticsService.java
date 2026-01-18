package com.sarthak.analyticsservice.service;

import com.sarthak.analyticsservice.dto.AnalyticsResponse;
import com.sarthak.analyticsservice.dto.ClickCountResponse;
import com.sarthak.analyticsservice.dto.ClickDetail;
import com.sarthak.analyticsservice.model.UrlClick;
import com.sarthak.analyticsservice.repository.UrlClickRepository;
import com.sarthak.common.event.UrlClickEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing URL click analytics.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AnalyticsService {
    
    private final UrlClickRepository urlClickRepository;
    
    private static final int MAX_RECENT_CLICKS = 100;
    
    /**
     * Save a click event from Kafka.
     */
    @Transactional
    public void saveClickEvent(UrlClickEvent event) {
        log.debug("Saving click event for: {}", event.getShortCode());
        
        UrlClick urlClick = UrlClick.builder()
                .shortCode(event.getShortCode())
                .ipAddress(event.getIpAddress())
                .userAgent(truncateUserAgent(event.getUserAgent()))
                .clickedAt(event.getClickedAt())
                .build();
        
        urlClickRepository.save(urlClick);
        log.debug("Saved click event for: {}", event.getShortCode());
    }
    
    /**
     * Get click count for a short code.
     */
    @Transactional(readOnly = true)
    public ClickCountResponse getClickCount(String shortCode) {
        log.debug("Getting click count for: {}", shortCode);
        
        long totalClicks = urlClickRepository.countByShortCode(shortCode);
        long uniqueVisitors = urlClickRepository.countUniqueIpsByShortCode(shortCode);
        
        return ClickCountResponse.builder()
                .shortCode(shortCode)
                .totalClicks(totalClicks)
                .uniqueVisitors(uniqueVisitors)
                .build();
    }
    
    /**
     * Get full analytics for a short code.
     */
    @Transactional(readOnly = true)
    public AnalyticsResponse getAnalytics(String shortCode) {
        log.debug("Getting analytics for: {}", shortCode);
        
        long totalClicks = urlClickRepository.countByShortCode(shortCode);
        long uniqueVisitors = urlClickRepository.countUniqueIpsByShortCode(shortCode);
        
        List<UrlClick> recentClicks = urlClickRepository
                .findByShortCodeOrderByClickedAtDesc(shortCode)
                .stream()
                .limit(MAX_RECENT_CLICKS)
                .collect(Collectors.toList());
        
        List<ClickDetail> clickDetails = recentClicks.stream()
                .map(this::mapToClickDetail)
                .collect(Collectors.toList());
        
        return AnalyticsResponse.builder()
                .shortCode(shortCode)
                .totalClicks(totalClicks)
                .uniqueVisitors(uniqueVisitors)
                .recentClicks(clickDetails)
                .build();
    }
    
    /**
     * Get click counts for all short codes.
     */
    @Transactional(readOnly = true)
    public List<ClickCountResponse> getAllClickCounts() {
        log.debug("Getting all click counts");
        
        List<java.util.Map<String, Object>> clickCounts = urlClickRepository.findAllClickCounts();
        List<java.util.Map<String, Object>> uniqueVisitors = urlClickRepository.findAllUniqueVisitors();
        
        // Map to store combined results
        java.util.Map<String, ClickCountResponse> combined = new java.util.HashMap<>();
        
        for (java.util.Map<String, Object> entry : clickCounts) {
            String sc = (String) entry.get("shortCode");
            long tc = ((Number) entry.get("totalClicks")).longValue();
            combined.put(sc, ClickCountResponse.builder().shortCode(sc).totalClicks(tc).build());
        }
        
        for (java.util.Map<String, Object> entry : uniqueVisitors) {
            String sc = (String) entry.get("shortCode");
            long uv = ((Number) entry.get("uniqueVisitors")).longValue();
            ClickCountResponse resp = combined.getOrDefault(sc, ClickCountResponse.builder().shortCode(sc).build());
            resp.setUniqueVisitors(uv);
            combined.put(sc, resp);
        }
        
        return new java.util.ArrayList<>(combined.values());
    }
    
    private ClickDetail mapToClickDetail(UrlClick urlClick) {
        return ClickDetail.builder()
                .ipAddress(urlClick.getIpAddress())
                .userAgent(urlClick.getUserAgent())
                .clickedAt(urlClick.getClickedAt())
                .build();
    }
    
    private String truncateUserAgent(String userAgent) {
        if (userAgent == null) {
            return null;
        }
        return userAgent.length() > 512 ? userAgent.substring(0, 512) : userAgent;
    }
}
