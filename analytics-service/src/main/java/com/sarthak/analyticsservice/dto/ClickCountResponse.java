package com.sarthak.analyticsservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClickCountResponse {
    
    private String shortCode;
    private long totalClicks;
    private long uniqueVisitors;
}
