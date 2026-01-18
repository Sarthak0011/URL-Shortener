package com.sarthak.analyticsservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Entity
@Table(name = "url_clicks", indexes = {
    @Index(name = "idx_short_code", columnList = "shortCode"),
    @Index(name = "idx_clicked_at", columnList = "clickedAt")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UrlClick {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "short_code", nullable = false, length = 10)
    private String shortCode;
    
    @Column(name = "ip_address", length = 45) // IPv6 max length
    private String ipAddress;
    
    @Column(name = "user_agent", length = 512)
    private String userAgent;
    
    @Column(name = "clicked_at", nullable = false)
    private LocalDateTime clickedAt;
}
