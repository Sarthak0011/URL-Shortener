package com.sarthak.analyticsservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsResponse {
    
    private String shortCode;
    private long totalClicks;
    private long uniqueVisitors;
    private List<ClickDetail> recentClicks;
}
